package dev.drsoran.moloko.test.comp.grammar.antlr.datetime.de;

import java.util.Collection;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Lexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dev.drsoran.moloko.grammar.antlr.datetime.de.TimeLexer;
import dev.drsoran.moloko.test.ITimeParserTestLanguage;
import dev.drsoran.moloko.test.MolokoTimeParserTestCase;
import dev.drsoran.moloko.test.TimeParserTestDataSource;
import dev.drsoran.moloko.test.TimeParserTestDataSource.ParseTimeEstimateTestData;


@RunWith( Parameterized.class )
public class TimeParserTimeEstimateTest extends MolokoTimeParserTestCase
{
   private final static ITimeParserTestLanguage TEST_LANGUAGE = new TimeParserTestLanguageDe();
   
   private final ParseTimeEstimateTestData testData;
   
   
   
   public TimeParserTimeEstimateTest( ParseTimeEstimateTestData testData )
   {
      this.testData = testData;
   }
   
   
   
   @Parameters( name = "{0}" )
   public static Collection< Object[] > parseTimeEstimateTestData()
   {
      final TimeParserTestDataSource testDataSource = new TimeParserTestDataSource( TEST_LANGUAGE );
      
      return testDataSource.getParseTimeEstimateTestData();
   }
   
   
   
   @Test
   public void testParseTimeEstimate()
   {
      parseTimeEstimate( testData.testString, testData.expectedMillis );
   }
   
   
   
   @Override
   protected Lexer createTimeLexer( ANTLRInputStream inputStream )
   {
      final Lexer lexer = new TimeLexer( inputStream );
      return lexer;
   }
}