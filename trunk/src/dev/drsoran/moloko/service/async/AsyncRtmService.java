package dev.drsoran.moloko.service.async;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mdt.rtm.ApplicationInfo;
import com.mdt.rtm.data.RtmAuth;
import com.mdt.rtm.data.RtmLists;
import com.mdt.rtm.data.RtmTasks;

import dev.drsoran.moloko.service.IRtmService;
import dev.drsoran.moloko.service.RtmServiceException;
import dev.drsoran.moloko.util.ResultCallback;


public class AsyncRtmService
{
   private final static String TAG = AsyncRtmService.class.getSimpleName();
   
   
   public interface Auth
   {
      public Future< ? > beginAuthorization( RtmAuth.Perms permission,
                                             ResultCallback< String > callback );
      


      public Future< ? > completeAuthorization( ResultCallback< String > callback );
      


      public Future< ? > checkAuthToken( String authToken,
                                         ResultCallback< RtmAuth > callback );
   }
   

   public interface Tasks
   {
      public Future< ? > getList( String listId,
                                  String filter,
                                  Date lastSync,
                                  ResultCallback< RtmTasks > callback );
   }
   

   public interface Lists
   {
      public Future< ? > getList( ResultCallback< RtmLists > callback );
   }
   
   // class AsyncRtmService
   
   private final Handler handler = new Handler();
   
   private final ExecutorService executor = Executors.newSingleThreadExecutor();
   
   private final ServiceConnection connection = new ServiceConnection()
   {
      public void onServiceConnected( ComponentName name, IBinder service )
      {
         syncService = IRtmService.Stub.asInterface( service );
         
         try
         {
            syncService.initialize( applicationInfo );
            Log.d( TAG, "Service connected." );
            
            for ( int i = 0; i < lazyConnectors.length; i++ )
            {
               if ( lazyConnectors[ i ] != null )
                  connectLazyConnector( lazyConnectors[ i ] );
            }
         }
         catch ( RemoteException e )
         {
            // In this case the service has crashed before we could even
            // do anything with it; we can count on soon being
            // disconnected (and then reconnected if it can be restarted)
            // so there is no need to do anything here.
         }
      }
      


      public void onServiceDisconnected( ComponentName name )
      {
         // This is called when the connection with the service has been
         // unexpectedly disconnected -- that is, its process crashed.
         syncService = null;
         
         Log.d( TAG, "Service disconnected." );
      }
      
   };
   
   private static final int SERVICE_PART_AUTH = 0;
   
   private static final int SERVICE_PART_TASKS = 1;
   
   private static final int SERVICE_PART_LISTS = 2;
   
   private final ILazyConnector[] lazyConnectors = new ILazyConnector[ 3 ];
   
   private ApplicationInfo applicationInfo = null;
   
   private IRtmService syncService = null;
   
   private Context context = null;
   
   

   public AsyncRtmService( Context context, ApplicationInfo applicationInfo )
   {
      this.context = context;
      this.applicationInfo = applicationInfo;
      connect( context );
   }
   


   public void shutdown()
   {
      executor.shutdown(); // Disable new tasks from being submitted
      
      try
      {
         // Wait a while for existing tasks to terminate
         if ( !executor.awaitTermination( 10, TimeUnit.SECONDS ) )
         {
            executor.shutdownNow(); // Cancel currently executing tasks
            // Wait a while for tasks to respond to being canceled
            if ( !executor.awaitTermination( 60, TimeUnit.SECONDS ) )
               Log.e( TAG, "Pool did not terminate" );
         }
      }
      catch ( InterruptedException ie )
      {
         // (Re-)Cancel if current thread also interrupted
         executor.shutdownNow();
         // Preserve interrupt status
         Thread.currentThread().interrupt();
      }
      
      for ( int i = 0; i < lazyConnectors.length; i++ )
      {
         if ( lazyConnectors[ i ] != null )
            lazyConnectors[ i ].disconnectService();
      }
      
      disconnect();
   }
   


   public boolean isConnected()
   {
      boolean is = false;
      
      is = syncService != null;
      
      return is;
   }
   


   public IRtmService sync()
   {
      return syncService;
   }
   


   public Auth auth()
   {
      ILazyConnector connector = lazyConnectors[ SERVICE_PART_AUTH ];
      
      if ( connector == null )
      {
         connector = lazyConnectors[ SERVICE_PART_AUTH ] = new AsyncAuthRtmService();
      }
      
      if ( !connector.isConnected() )
      {
         connectLazyConnector( connector );
      }
      
      return (Auth) connector;
   }
   


   public Tasks tasks()
   {
      ILazyConnector connector = lazyConnectors[ SERVICE_PART_TASKS ];
      
      if ( connector == null )
      {
         connector = lazyConnectors[ SERVICE_PART_AUTH ] = new AsyncTasksRtmService();
      }
      
      if ( !connector.isConnected() )
      {
         connectLazyConnector( connector );
      }
      
      return (Tasks) connector;
   }
   


   public Lists lists()
   {
      ILazyConnector connector = lazyConnectors[ SERVICE_PART_LISTS ];
      
      if ( connector == null )
      {
         connector = lazyConnectors[ SERVICE_PART_LISTS ] = new AsyncListsRtmService();
      }
      
      if ( !connector.isConnected() )
      {
         connectLazyConnector( connector );
      }
      
      return (Lists) connector;
   }
   


   public static String getExceptionCause( final Exception e )
   {
      if ( e instanceof RtmServiceException )
      {
         return ( (RtmServiceException) e ).rtmCause;
      }
      else
      {
         return e.getMessage();
      }
   }
   


   public static int getExceptionCode( final Exception e )
   {
      if ( e instanceof RtmServiceException )
      {
         return ( (RtmServiceException) e ).errorCode;
      }
      else
      {
         return 0;
      }
   }
   


   // / PRIVATE IMPLEMENTATIONS ///
   
   private void connect( final Context context )
   {
      // Establish a couple connections with the service, binding
      // by interface names. This allows other applications to be
      // installed that replace the remote service by implementing
      // the same interface.
      context.bindService( new Intent( IRtmService.class.getName() ),
                           connection,
                           Context.BIND_AUTO_CREATE );
   }
   


   private void disconnect()
   {
      if ( syncService != null )
      {
         /*
          * try { syncService.unregisterCallback( rtmServiceCallback ); } catch ( RemoteException e ) { // There is
          * nothing special we need to do if the service // has crashed. }
          */

         context.unbindService( connection );
      }
   }
   


   private void connectLazyConnector( ILazyConnector connector )
   {
      if ( isConnected() )
      {
         try
         {
            connector.connectService( syncService, handler, executor );
         }
         
         // If we fail here we simply do not connect. So the next call
         // will try to connect again.
         catch ( InterruptedException e )
         {
            return;
         }
         catch ( ExecutionException e )
         {
            return;
         }
      }
   }
}
