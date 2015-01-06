/* 
 *	Copyright (c) 2013 Ronny R�hricht
 *
 *	This file is part of MolokoTest.
 *
 *	MolokoTest is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	MolokoTest is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with MolokoTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Contributors:
 * Ronny R�hricht - implementation
 */

package dev.drsoran.rtm.test.comp.parsing.grammar.antlr.recurrence;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dev.drsoran.rtm.parsing.grammar.ANTLRBailOutErrorListener;
import dev.drsoran.rtm.parsing.grammar.antlr.recurrence.RecurrencePatternLexer;
import dev.drsoran.rtm.parsing.grammar.antlr.recurrence.RecurrencePatternParser;
import dev.drsoran.rtm.parsing.lang.RecurrenceSentenceLanguage;
import dev.drsoran.rtm.parsing.recurrence.RecurrenceSentenceEvaluator;
import dev.drsoran.rtm.test.TestDateFormatter;
import dev.drsoran.rtm.test.testdatasources.RecurrenceSentenceTestDataSource;
import dev.drsoran.rtm.test.testdatasources.RecurrenceSentenceTestLanguageEn;


@RunWith( Parameterized.class )
public class RecurrenceSentenceTest
{
   private final RecurrenceSentenceTestDataSource.TestData testData;
   
   private static RecurrenceSentenceLanguage language = RecurrenceSentenceTestLanguageEn.get();
   
   
   
   public RecurrenceSentenceTest(
      RecurrenceSentenceTestDataSource.TestData testData )
   {
      this.testData = testData;
   }
   
   
   
   @Parameters( name = "{0}" )
   public static Collection< Object[] > getTestData()
   {
      return new RecurrenceSentenceTestDataSource( language ).getTestData();
   }
   
   
   
   @Test
   public void testRecurrenceSentences()
   {
      final CharStream stream = new ANTLRInputStream( testData.pattern );
      final Lexer lexer = new RecurrencePatternLexer( stream );
      lexer.addErrorListener( new ANTLRBailOutErrorListener() );
      
      final TokenStream tokenStream = new CommonTokenStream( lexer );
      final RecurrencePatternParser parser = new RecurrencePatternParser( tokenStream );
      parser.getInterpreter().setPredictionMode( PredictionMode.SLL );
      
      try
      {
         parser.setErrorHandler( new BailErrorStrategy() );
         final ParseTree tree = parser.parseRecurrencePattern();
         
         final RecurrenceSentenceEvaluator evaluator = new RecurrenceSentenceEvaluator( TestDateFormatter.get(),
                                                                                        language,
                                                                                        testData.every );
         evaluator.visit( tree );
         
         final String sentence = evaluator.getSentence();
         
         assertThat( "Wrong sentence for <" + testData.pattern + ">",
                     sentence,
                     is( testData.sentence ) );
      }
      catch ( ParseCancellationException e )
      {
         fail( "Parsing <" + testData.pattern + "> failed" );
      }
   }
}