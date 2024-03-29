/**
 * ViewGroup that arranges child views in a similar way to text, with them laid
 * out one line at a time and "wrapping" to
 * the next line as needed.
 * 
 * Code licensed under CC-by-SA
 * 
 * @author Henrik Gustafsson
 * @see http
 *      ://stackoverflow.com/questions/549451/line-breaking-widget-layout-for
 *      -android
 * @license http://creativecommons.org/licenses/by-sa/2.5/
 * 
 */

package dev.drsoran.moloko.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import dev.drsoran.moloko.R;


public class WrappingLayout extends ViewGroup
{
   private WrappingLayout.LayoutParams wrappingLayoutParams = new WrappingLayout.LayoutParams( 1,
                                                                                               1 );
   
   private int line_height;
   
   
   public static class LayoutParams extends ViewGroup.LayoutParams
   {
      public final int horizontal_spacing;
      
      public final int vertical_spacing;
      
      

      /**
       * @param horizontal_spacing
       *           Pixels between items, horizontally
       * @param vertical_spacing
       *           Pixels between items, vertically
       */
      public LayoutParams( int horizontal_spacing, int vertical_spacing )
      {
         super( 0, 0 );
         this.horizontal_spacing = horizontal_spacing;
         this.vertical_spacing = vertical_spacing;
      }
   }
   
   

   public WrappingLayout( Context context, AttributeSet attrs )
   {
      this( context, attrs, R.attr.wrappingLayoutStyle );
   }
   


   public WrappingLayout( Context context, AttributeSet attrs, int defStyle )
   {
      super( context, attrs, defStyle );
      
      // WrappingLayout
      final TypedArray array = context.obtainStyledAttributes( attrs,
                                                               R.styleable.WrappingLayout,
                                                               defStyle,
                                                               0 );
      
      final int hor_spc = array.getDimensionPixelOffset( R.styleable.WrappingLayout_horizontal_spacing,
                                                         1 );
      final int ver_spc = array.getDimensionPixelOffset( R.styleable.WrappingLayout_vertical_spacing,
                                                         1 );
      
      wrappingLayoutParams = new WrappingLayout.LayoutParams( hor_spc, ver_spc );
      
      array.recycle();
   }
   


   @Override
   protected void onFinishInflate()
   {
      super.onFinishInflate();
      
      for ( int i = 0, cnt = getChildCount(); i < cnt; ++i )
      {
         final View childView = getChildAt( i );
         if ( !checkLayoutParams( childView.getLayoutParams() ) )
            childView.setLayoutParams( wrappingLayoutParams );
      }
   }
   


   @Override
   protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
   {
      assert ( MeasureSpec.getMode( widthMeasureSpec ) != MeasureSpec.UNSPECIFIED );
      
      final int width = MeasureSpec.getSize( widthMeasureSpec )
         - getPaddingLeft() - getPaddingRight();
      int height = MeasureSpec.getSize( heightMeasureSpec ) - getPaddingTop()
         - getPaddingBottom();
      final int count = getChildCount();
      int line_height = 0;
      
      int xpos = getPaddingLeft();
      int ypos = getPaddingTop();
      
      for ( int i = 0; i < count; i++ )
      {
         final View child = getChildAt( i );
         if ( child.getVisibility() != GONE )
         {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            
            int childHeightMeasureSpec;
            if ( MeasureSpec.getMode( heightMeasureSpec ) == MeasureSpec.AT_MOST )
            {
               childHeightMeasureSpec = MeasureSpec.makeMeasureSpec( height,
                                                                     MeasureSpec.AT_MOST );
            }
            else
            {
               childHeightMeasureSpec = MeasureSpec.makeMeasureSpec( 0,
                                                                     MeasureSpec.UNSPECIFIED );
            }
            
            child.measure( MeasureSpec.makeMeasureSpec( width,
                                                        MeasureSpec.AT_MOST ),
                           childHeightMeasureSpec );
            
            final int childw = child.getMeasuredWidth();
            line_height = Math.max( line_height, child.getMeasuredHeight()
               + lp.vertical_spacing );
            
            if ( xpos + childw > width )
            {
               xpos = getPaddingLeft();
               ypos += line_height;
            }
            
            xpos += childw + lp.horizontal_spacing;
         }
      }
      this.line_height = line_height;
      
      if ( MeasureSpec.getMode( heightMeasureSpec ) == MeasureSpec.UNSPECIFIED )
      {
         height = ypos + line_height;
         
      }
      else if ( MeasureSpec.getMode( heightMeasureSpec ) == MeasureSpec.AT_MOST )
      {
         if ( ypos + line_height < height )
         {
            height = ypos + line_height;
         }
      }
      setMeasuredDimension( width, height );
   }
   


   @Override
   protected ViewGroup.LayoutParams generateDefaultLayoutParams()
   {
      return wrappingLayoutParams;
   }
   


   @Override
   protected boolean checkLayoutParams( ViewGroup.LayoutParams p )
   {
      if ( p instanceof LayoutParams )
         return true;
      return false;
   }
   


   @Override
   protected void onLayout( boolean changed, int l, int t, int r, int b )
   {
      final int count = getChildCount();
      final int width = r - l;
      int xpos = getPaddingLeft();
      int ypos = getPaddingTop();
      
      for ( int i = 0; i < count; i++ )
      {
         final View child = getChildAt( i );
         if ( child.getVisibility() != GONE )
         {
            final int childw = child.getMeasuredWidth();
            final int childh = child.getMeasuredHeight();
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if ( xpos + childw > width )
            {
               xpos = getPaddingLeft();
               ypos += line_height;
            }
            child.layout( xpos, ypos, xpos + childw, ypos + childh );
            xpos += childw + lp.horizontal_spacing;
         }
      }
   }
}
