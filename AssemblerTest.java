package project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssemblerTest {
    @Test
    @DisplayName("ori $t0, $zero, 15")
    void Test1() throws Exception {
        // arrange
        Assembler assembler = new Assembler();

        // act
        Instruction instruction = assembler.ParseInstruction("ori $t0, $zero, 15");
        String assemblyCode = assembler.Assemble(instruction);

        // assert
        assertEquals("0x3408000f", assemblyCode);
    }

    @Test
    @DisplayName("addi $t1, $zero, 3")
    void Test2() throws Exception {
        // arrange
        Assembler assembler = new Assembler();

        // act
        Instruction instruction = assembler.ParseInstruction("addi $t1, $zero, 3");
        String assemblyCode = assembler.Assemble(instruction);

        // assert
        assertEquals("0x20090003", assemblyCode);
    }

    @Test
    @DisplayName("add $t1, $zero, $t1")
    void Test3() throws Exception {
        // arrange
        Assembler assembler = new Assembler();

        // act
        Instruction instruction = assembler.ParseInstruction("add $t1, $zero, $t1");
        String assemblyCode = assembler.Assemble(instruction);

        // assert
        assertEquals("0x00094820", assemblyCode);
    }

    @Test
    @DisplayName("sub $t2, $t0, $s0")
    void Test4() throws Exception {
        // arrange
        Assembler assembler = new Assembler();

        // act
        Instruction instruction = assembler.ParseInstruction("sub $t2, $t0, $s0");
        String assemblyCode = assembler.Assemble(instruction);

        // assert
        assertEquals("0x01105022", assemblyCode);
    }

    @Test
    @DisplayName("slt $s0, $t1, $t3")
    void Test5() throws Exception {
        // arrange
        Assembler assembler = new Assembler();

        // act
        Instruction instruction = assembler.ParseInstruction("slt $s0, $t1, $t3");
        String assemblyCode = assembler.Assemble(instruction);

        // assert
        assertEquals("0x012b802a", assemblyCode);
    }

    @Test
    @DisplayName("andi $v0, $zero, 1")
    void Test6() throws Exception {
        // arrange
        Assembler assembler = new Assembler();

        // act
        Instruction instruction = assembler.ParseInstruction("andi $v0, $zero, 1");
        String assemblyCode = assembler.Assemble(instruction);

        // assert
        assertEquals("0x30020001", assemblyCode);
    }
}
