package org.jasome.executive;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;

@RunWith(BlockJUnit4ClassRunner.class)
public class CommandLineExecutiveTest {

    @Test
    public void test_that_overall_correctly_operates() throws IOException, ParseException {
        // Given
        String[] args = {"./src/main"};
        // When
        CommandLineExecutive.main(args);
        // Then
    }

}
