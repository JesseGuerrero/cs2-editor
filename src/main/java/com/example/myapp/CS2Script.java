package com.example.myapp;

import java.sql.SQLOutput;
import java.util.*;

public class CS2Script {

    private int id;
    private String name;
    private List<HashMap<String,String>> args;
    private List<HashMap<String,Object>> variables;
    private Object returnType;

    public String decode(int id, byte[] data) throws Exception {
        InputStream stream = new InputStream(data);
        stream.setOffset(stream.getLength() - 2);
        int switchBlockSize = stream.readUnsignedShort();
        int instructionLength = stream.getLength() - 2 - switchBlockSize - 16;

        stream.setOffset(instructionLength);
        int codeSize = stream.readInt();
        //variables
        int intLocalsCount = stream.readUnsignedShort();
        int stringLocalsCount = stream.readUnsignedShort();
        int longLocalsCount = stream.readUnsignedShort();

        //args (they go before or after variables...?)
        int intArgs = stream.readUnsignedShort();
        int stringArgs = stream.readUnsignedShort();
        int longArgs = stream.readUnsignedShort();

        this.args = new ArrayList<>();
        int argIndex = 0;
        // Add int arguments
        for (int i = 0; i < intArgs; i++) {
            HashMap<String, String> arg = new HashMap<>();
            arg.put("type", "int");
            arg.put("name", "arg" + argIndex);
            this.args.add(arg);
            argIndex++;
        }

        // Add string arguments
        for (int i = 0; i < stringArgs; i++) {
            HashMap<String, String> arg = new HashMap<>();
            arg.put("type", "string");
            arg.put("name", "arg" + argIndex);
            this.args.add(arg);
            argIndex++;
        }

        // Add long arguments
        for (int i = 0; i < longArgs; i++) {
            HashMap<String, String> arg = new HashMap<>();
            arg.put("type", "long");
            arg.put("name", "arg" + argIndex);
            this.args.add(arg);
            argIndex++;
        }

        this.variables = new ArrayList<>();
        int iIndex = 0;
        int sIndex = 0;
        int lIndex = 0;

        // Initialize variables list if not already done
        if (this.variables == null) {
            this.variables = new ArrayList<>();
        }

        // Add int argument variables
        for (int i = 0; i < intArgs; i++) {
            int index = iIndex++;
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("type", "int");
            variable.put("vType", "arg");
            variable.put("name", (this.args.get(index)).get("name"));
            variable.put("index", index);
            this.variables.add(variable);
        }

        // Add string argument variables
        for (int i = 0; i < stringArgs; i++) {
            int index = sIndex++;
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("type", "string");
            variable.put("vType", "arg");
            variable.put("name", (this.args.get(index)).get("name"));
            variable.put("index", index);
            this.variables.add(variable);
        }

        // Add long argument variables
        for (int i = 0; i < longArgs; i++) {
            int index = lIndex++;
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("type", "long");
            variable.put("vType", "arg");
            variable.put("name", (this.args.get(index)).get("name"));
            variable.put("index", index);
            this.variables.add(variable);
        }

        // Add int local variables
        for (int i = 0; i < intLocalsCount - intArgs; i++) {
            int index = iIndex++;
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("type", "int");
            variable.put("vType", "var");
            variable.put("name", "iVar" + index);
            variable.put("index", index);
            this.variables.add(variable);
        }

        // Add string local variables
        for (int i = 0; i < stringLocalsCount - stringArgs; i++) {
            int index = sIndex++;
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("type", "string");
            variable.put("vType", "var");
            variable.put("name", "sVar" + i);
            variable.put("index", index);
            this.variables.add(variable);
        }

        // Add long local variables
        for (int i = 0; i < longLocalsCount - longArgs; i++) {
            int index = lIndex++;
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("type", "long");
            variable.put("vType", "var");
            variable.put("name", "lVar" + i);
            variable.put("index", index);
            this.variables.add(variable);
        }

        // Initialize value arrays
        List<Integer> iValues = new ArrayList<>();
        List<String> sValues = new ArrayList<>();
        List<Long> lValues = new ArrayList<>();

        int switchCount = stream.readUnsignedByte();

        if (switchCount > 0) {
            List switchMap = new ArrayList<>();
            for (int i = 0; i < switchCount; i++) {
                Map<Integer, Integer> switchEntry = new HashMap<>();
                int size = stream.readUnsignedShort();
                while (size-- > 0) {
                    int casee = stream.readInt();
                    int addr = stream.readInt();
                    switchEntry.put(addr, casee);
                }
                switchMap.add(switchEntry);
            }
        }

        stream.setOffset(0);


        String unknown = stream.readNullString(); // Required for offset
//        List<Map<String, Object>> instructions = new ArrayList<>();

//        int opCount = 0;
        String outputText = "function script" + id + "(";
        String arguments = "";
        if(this.args.size() > 0) {
            for (HashMap<String, String> arg : this.args) {
                arguments = outputText + arg.get("name") + ": " + arg.get("type") + ", ";
            }
            arguments = arguments.substring(0, arguments.length() - 2);
        }
        outputText = outputText + arguments + "): ";
        System.out.println(this.args);
        System.out.println(this.variables);

        ArrayList<List> stack = new ArrayList<>();
        while (stream.getOffset() < instructionLength) {
            int opcode = stream.readUnsignedShort();
            if (opcode < 0) {
                throw new IllegalArgumentException("Invalid instruction opcode: " + opcode);
            }
            ClientScriptOpCode opCode = ClientScriptOpCode.getByOpcode(opcode);
            int output;
            if(opCode.hasIntConstant) {
                output = stream.readInt();
            } else {
                output = stream.readUnsignedByte();
            }
            if (opCode == ClientScriptOpCode.RETURN) {
                if(output == 0)
                    outputText = outputText + "void {\n";
                else
                    throw new Exception("Unkown return type");
                stack.add(Arrays.asList(ClientScriptOpCode.RETURN, "return", output));
            }
            if(opCode == ClientScriptOpCode.LOAD_INT)
                stack.add(Arrays.asList(ClientScriptOpCode.LOAD_INT,  this.variables.get(output).get("name"), output));
            if(opCode == ClientScriptOpCode.CALL_CS2)
                stack.add(Arrays.asList(ClientScriptOpCode.CALL_CS2, "script_" + output, output));
            System.out.println(opCode.opcode + " " + opCode.name() + " -> " + output);
        }
        System.out.println(stack);
        String code = "";
        boolean indent = true;
        boolean deindent = false;
        String indents = "";
        for(List plate : stack) {
            ClientScriptOpCode opcode = (ClientScriptOpCode) plate.get(0);
            String content = (String)plate.get(1);
            int meta = (int)plate.get(2);
            if(indent == true)
                indents = indents + "    ";
            if(deindent == true)
                indents = indents.substring(0, indents.length() - 4);

            indent = false;
            deindent = false;
            switch (opcode) {
                case ClientScriptOpCode.LOAD_INT -> {
                    code = content + code;
                    code = "(" + code + ")";
                }
                case ClientScriptOpCode.CALL_CS2 -> {code = content + code;}
                case ClientScriptOpCode.RETURN -> {code = indents + code + "\n" + indents + content;}
            }
        }
        System.out.println(outputText + code + "\n}");

        return "test";
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<HashMap<String, String>> getArgs() {
        return args;
    }

    public List<HashMap<String,Object>> getVariables() {
        return variables;
    }

    public Object getReturnType() {
        return returnType;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArgs(List<HashMap<String, String>> args) {
        this.args = args;
    }

    public void setVariables(List<HashMap<String,Object>> variables) {
        this.variables = variables;
    }

    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }
}