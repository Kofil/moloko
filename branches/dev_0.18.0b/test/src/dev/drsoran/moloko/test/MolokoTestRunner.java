/* 
 *	Copyright (c) 2012 Ronny R�hricht
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

package dev.drsoran.moloko.test;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.ShadowMap;
import org.robolectric.bytecode.ShadowMap.Builder;

import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.moloko.test.shadows.ACRAShadow;
import dev.drsoran.moloko.test.shadows.ContentProviderClientShadow;
import dev.drsoran.moloko.test.shadows.ContentResolverShadow;


abstract class MolokoTestRunner extends RobolectricTestRunner
{
   private MolokoApp molokoApp;
   
   
   
   protected MolokoTestRunner( Class< ? > testClass )
      throws InitializationError
   {
      super( testClass );
   }
   
   
   
   public MolokoApp getMolokoApp()
   {
      return molokoApp;
   }
   
   
   
   @Override
   protected ShadowMap createShadowMap()
   {
      final ShadowMap shadowMap = super.createShadowMap();
      
      final Builder shadowMapBuilder = shadowMap.newBuilder();
      shadowMapBuilder.addShadowClass( ACRAShadow.class );
      shadowMapBuilder.addShadowClass( ContentResolverShadow.class );
      shadowMapBuilder.addShadowClass( ContentProviderClientShadow.class );
      
      return shadowMapBuilder.build();
   }
   
   
   
   public abstract String getValuesResQualifiers();
}