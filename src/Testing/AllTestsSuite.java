package Testing;

import Game.Board;
import Game.Pieces.Queen;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Specify the runner for the test suite
@RunWith(Suite.class)

// Specify the test classes that should be included in the suite
@Suite.SuiteClasses({
        BoardTests.class
})

public class AllTestsSuite {
    // This class remains empty
    // It is used only as a holder for the above annotations
}

