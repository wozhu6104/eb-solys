package test.com.elektrobit.ebrace.core.systemmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.systemmodel.api.SystemModelAccess;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelNode;
import com.elektrobit.ebrace.core.systemmodel.api.ViewModelGenerator;
import com.elektrobit.ebrace.core.systemmodel.impl.SystemModelAccessService;

public class SystemModelTest
{

    private static final String CLASSES = "classes";

    private static final String ELEMENT_PATTERNS = "elementPatterns";

    private static final String expected = "sys,,{}\n" + "subsys1,sys,{}\n" + "AppA,subsys1,{}\n" + "Comp3,AppA,{}\n"
            + "Comp2,AppA,{}\n" + "AppX,subsys1,{}\n" + "Comp9,AppX,{}\n" + "subsys2,sys,{}\n" + "AppC,subsys2,{}\n"
            + "Comp4,AppC,{}\n" + "Comp5,AppC,{}\n" + "AppD,subsys2,{}\n" + "Comp3,AppD,{}\n"
            + "sys.subsys2.AppC.Comp4,sys.subsys2.AppC.Comp5,{}\n"
            + "sys.subsys2.AppC.Comp5,sys.subsys1.AppX.Comp9,{}\n";

    public final String inputModelPath = "resources/inputModel.json";
    public final String inputModelPatternPath = "resources/inputModelPatterns.json";

    private SystemModelAccess modelAccess = null;

    @Before
    public void setup()
    {
        modelAccess = new SystemModelAccessService();
    }

    @Test
    public void loadModelCheckNotNull() throws FileNotFoundException
    {
        assertNotNull( modelAccess.initFromFile( inputModelPath ) );
    }

    @Test
    public void loadModelCheckEdges() throws FileNotFoundException
    {
        ;
        assertEquals( 2, modelAccess.initFromFile( inputModelPath ).getEdges().size() );
    }

    @Test
    public void loadModelCheckNodes() throws FileNotFoundException
    {
        assertEquals( 13, modelAccess.initFromFile( inputModelPath ).getNodes().size() );
    }

    @Test
    public void generateModel() throws FileNotFoundException
    {
        assertEquals( expected,
                      modelAccess.generate( modelAccess.initFromFile( inputModelPath ), new ViewModelGenerator()
                      {

                          @Override
                          public String handleNode(String name, String parent, Map<String, Object> annotations)
                          {

                              return name + "," + parent + "," + annotations + "\n";
                          }

                          @Override
                          public String handleEdge(String from, String to, Map<String, Object> annotations)
                          {
                              return from + "," + to + "," + annotations + "\n";
                          }

                          @Override
                          public String start()
                          {
                              return "";
                          }

                          @Override
                          public String end()
                          {
                              return "";
                          }

                          @Override
                          public String nodesStart()
                          {
                              return "";
                          }

                          @Override
                          public String nodesEnd()
                          {
                              return "";
                          }

                          @Override
                          public String edgesStart()
                          {
                              return "";
                          }

                          @Override
                          public String edgesEnd()
                          {
                              return "";
                          }

                          @Override
                          public String separator()
                          {
                              return "";
                          }
                      } ) );
    }

    @Test
    public void patternModelCheckCount() throws FileNotFoundException
    {
        List<SystemModelNode> nodes = modelAccess.initFromFile( inputModelPatternPath ).getNodes();
        assertEquals( 4, nodes.size() );
    }

    @Test
    public void patternModelCheckNoPattern() throws FileNotFoundException
    {
        List<SystemModelNode> nodes = modelAccess.initFromFile( inputModelPatternPath ).getNodes();
        SystemModelNode noPatternNode = nodes.get( 1 );
        assertEquals( 0, noPatternNode.getAnnotations().size() );
    }

    @Test
    public void patternModelCheckOnePatternOneClass() throws FileNotFoundException
    {
        List<SystemModelNode> nodes = modelAccess.initFromFile( inputModelPatternPath ).getNodes();
        SystemModelNode onePatternNode = nodes.get( 2 );
        assertEquals( 2, onePatternNode.getAnnotations().size() );
        assertEquals( "[\".*solys.*\"]", onePatternNode.getAnnotations().get( ELEMENT_PATTERNS ) );
        assertEquals( "group", onePatternNode.getAnnotations().get( CLASSES ) );
    }

    @Test
    public void patternModelCheckTwoPatterns() throws FileNotFoundException
    {
        List<SystemModelNode> nodes = modelAccess.initFromFile( inputModelPatternPath ).getNodes();
        SystemModelNode twoPatternsNode = nodes.get( 3 );
        assertEquals( 1, twoPatternsNode.getAnnotations().size() );
        assertEquals( "[\".*service.*\",\".*daemon.*\"]", twoPatternsNode.getAnnotations().get( ELEMENT_PATTERNS ) );
    }
}
