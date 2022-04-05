package com.example.geektrust.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.geektrust.constants.Constants.MAX_SIZE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberServiceTest {

    private MemberService memberService;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    public void setUpStreams() {
        memberService = new MemberService(MAX_SIZE);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }


    @ParameterizedTest
    @MethodSource("processCommandsArguments")
    void processCommandsTest(List<String> input, String output) {
        input.forEach(memberService::processCommands);
        assertArrayEquals(output.split("\\s+"), outContent.toString().trim().split("\\s+"));
    }

    private static Stream<Arguments> processCommandsArguments() throws URISyntaxException, IOException {
        Path input1 = Paths.get(getURI("sample/input1.txt"));
        Path output1 = Paths.get(getURI("sample/output1.txt"));
        Path input2 = Paths.get(getURI("sample/input2.txt"));
        Path output2 = Paths.get(getURI("sample/output2.txt"));
        Path input3 = Paths.get(getURI("sample/input3.txt"));
        Path output3 = Paths.get(getURI("sample/output3.txt"));

        List<String> inputList1 = Files.lines(input1).collect(Collectors.toList());
        String outputString1 = Files.lines(output1).collect(Collectors.joining("\n"));
        List<String> inputList2 = Files.lines(input2).collect(Collectors.toList());
        String outputString2 = Files.lines(output2).collect(Collectors.joining("\n"));
        List<String> inputList3 = Files.lines(input3).collect(Collectors.toList());
        String outputString3 = Files.lines(output3).collect(Collectors.joining("\n"));

        return Stream.of(
                Arguments.of(inputList1, outputString1),
                Arguments.of(inputList2, outputString2),
                Arguments.of(inputList3, outputString3)
        );
    }


    private static URI getURI(String path) throws URISyntaxException {
        return ClassLoader.getSystemResource(path).toURI();
    }
}
