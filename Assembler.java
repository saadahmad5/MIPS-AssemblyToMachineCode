package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

public class Assembler {
    private final String[] HexValues = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private final List<InstructionFormat> InstructionsList;
    private final Map<String, Integer> Registers;
    private final ArrayList<String> AssemblyCodeBits;
    public Assembler() throws Exception {
        InstructionsList = new ArrayList<>();
        Registers = new HashMap<>();
        AssemblyCodeBits = new ArrayList<>();
        Initialize();
    }

    public void Run() throws Exception {
        System.out.println("Enter your MIPS Assembly code: ('q' to quit)");
        String prompt = InstructionPrompt();
        while (!Objects.equals(prompt, "q")) {
            Instruction Instruction;
            Instruction = ParseInstruction(prompt);
            Assemble(Instruction);
            prompt = InstructionPrompt();
        }
    }

    public Instruction ParseInstruction(String instruction) throws Exception {
        Instruction Instruction = new Instruction();
        try {
            String[] operands = instruction.replaceAll(",", "").split(" ");
            Instruction.Mnemonic = operands[0];
            Instruction.Argument0 = operands[1];
            Instruction.Argument1 = operands[2];
            Instruction.Argument2 = operands[3];
        } catch (Exception e) {
            throw new InputMismatchException("Invalid instruction/ set of operands");
        }
        return Instruction;
    }

    public String Assemble(Instruction instruction) {
        for (InstructionFormat instructionFormat: InstructionsList) {
            if (instructionFormat.Mnemonic.equals(instruction.Mnemonic)) {
                System.out.print(instructionFormat.InstructionType);
                switch (instructionFormat.InstructionType) {
                    case R:
                        InsertToAssemblyCodeBits(PrependBits(6, String.valueOf(0))); // Opcode has 6 bits
                        InsertToAssemblyCodeBits(PrependBits(5, String.valueOf(Registers.get(instruction.Argument1)))); // rs has 5 bits
                        InsertToAssemblyCodeBits(PrependBits(5, String.valueOf(Registers.get(instruction.Argument2)))); // rt has 5 bits
                        InsertToAssemblyCodeBits(PrependBits(5, String.valueOf(Registers.get(instruction.Argument0)))); // rd has 5 bits
                        InsertToAssemblyCodeBits(PrependBits(5, String.valueOf(0))); // Shamt has 5 bits
                        InsertToAssemblyCodeBits(PrependBits(6, String.valueOf(instructionFormat.Funct))); // Funct has 6 bits
                        break;
                    case I:
                        InsertToAssemblyCodeBits(PrependBits(6, String.valueOf(instructionFormat.Opcode))); // Opcode has 6 bits
                        InsertToAssemblyCodeBits(PrependBits(5, String.valueOf(Registers.get(instruction.Argument1)))); // rs has 5 bits
                        InsertToAssemblyCodeBits(PrependBits(5, String.valueOf(Registers.get(instruction.Argument0)))); // rt has 5 bits
                        InsertToAssemblyCodeBits(PrependBits(16, String.valueOf(instruction.Argument2))); // immediate has 16 bits
                        break;
                    case J:
                        throw new NullPointerException("J Type not implemented");
                    default:
                        throw new RuntimeException("Unhandled instruction type");
                }
            }
        }
        if (AssemblyCodeBits.size() > 0) {
            ShowAssemblyCodeBits();
            String[] BitCode = GetAssemblyCodeBits();
            StringBuilder Code = new StringBuilder("0x");
            for (String bitcode: BitCode) {
                Code.append(HexValues[Integer.parseInt(bitcode, 2)]);
            }
            System.out.println(Code);
            return Code.toString();
        }
        throw new NullPointerException("Instruction not implemented/ not compatible");
    }

    private void ShowAssemblyCodeBits() {
        for (String binary: AssemblyCodeBits) {
            System.out.print(" " + binary);
        }
        System.out.println();
    }

    private String[] GetAssemblyCodeBits() {
        String assemblyBits = Arrays.stream(AssemblyCodeBits.stream().toArray(String[]::new)).reduce((a, b) -> a + b).get();
        String[] _assemblyBits = new String[8];
        Arrays.fill(_assemblyBits, 0, 8, "");
        int i = 0, j = 0, spaceAfter = 4;
        for (char ch: assemblyBits.toCharArray()) {
            System.out.print(ch);
            _assemblyBits[j] += ch;
            i++;
            if (i % spaceAfter == 0) {
                System.out.print(" ");
                j++;
            }
        }
        System.out.println();
        AssemblyCodeBits.clear();
        return _assemblyBits;
    }

    private String InstructionPrompt() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">> ");
        return scanner.nextLine();
    }

    private String PrependBits(int n, String value) {
        int intVal = Integer.parseInt(value);
        value = Integer.toBinaryString(intVal);
        while (value.length() < n) {
            value = '0' + value;
        }
        return value;
    }

    private void InsertToAssemblyCodeBits(String value) {
        AssemblyCodeBits.add(value);
    }

    private void Initialize() throws Exception {
        try (Scanner scanner = new Scanner(new File("C:/Users/saada/source/repos/computerArch/src/project/MIPSGreenSheet.csv"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] assemblyCode = line.split(",");
                InstructionFormat instructionFormat = new InstructionFormat();
                instructionFormat.Mnemonic = assemblyCode[0].trim();
                switch (assemblyCode[1].trim()) {
                    case "R":
                        instructionFormat.InstructionType = InstructionType.R;
                        instructionFormat.Funct = new BigInteger(assemblyCode[2].trim(), 16).byteValue();
                        break;
                    case "I":
                        instructionFormat.InstructionType = InstructionType.I;
                        instructionFormat.Opcode = new BigInteger(assemblyCode[2].trim(), 16).byteValue();
                        break;
                    case "J":
                        instructionFormat.InstructionType = InstructionType.J;
                        break;
                    default:
                        throw new RuntimeException("Unhandled instruction type: " + assemblyCode[1]);
                }
                InstructionsList.add(instructionFormat);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found at: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Unhandled exception at: " + e.getMessage());
        }

        try(Scanner scanner = new Scanner(new File("C:/Users/saada/source/repos/computerArch/src/project/Registers.csv"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] register = line.split(",");
                Registers.put(register[0].trim(), Integer.valueOf(register[1].trim()));
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found at: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Unhandled exception at: " + e.getMessage());
        }
    }
}
