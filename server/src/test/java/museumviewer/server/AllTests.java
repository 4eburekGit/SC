package museumviewer.server;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ ChicagoDataTest.class, DBManagerTest.class, MainTest.class })
public class AllTests {

}
