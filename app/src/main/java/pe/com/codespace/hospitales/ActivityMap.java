package pe.com.codespace.hospitales;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ActivityMap extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap map;
    SQLiteHelperHospitales myDBHelper;
    private LatLng coordenadas;
    private LatLng misCoordenadas;
    Location currentLocation;
    private static LocationClient mLocationClient;
    private static LocationRequest mLocationRequest;
    int id;
    int tipoCentros, idCentro, tipoBusqueda, institucion;
    String nombreCentro;
    String latitud, longitud;
    String nombre, direccion;
    String telefono1, telefono2;

    CameraUpdate cameraUpdate;
    private final static int ZOOM_CENTRO = 16;
    private final static int ZOOM_DEPARTAMENTO = 9;
    private final static int ZOOM_CERCANOS = 12;
    private final static int ZOOM_PROVINCIA = 14;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private static final int CERCANOS = 1;
    private static final int ESSALUD = 2;
    private static final int MINSA = 3;
    private static final int SISOL = 4;
    private static final int PRIVADOS = 5;
    private static final int FFAAPP = 6;

    private static final int UNOPORDEPARTAMENTO = 1;
    private static final int UNOPORRED = 2;
    private static final int TODOSPORDEPARTAMENTO = 3;
    private static final int TODOSPORRED = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationClient = new LocationClient(this, this, this);
        if(isGooglePlayServicesAvailable()){
            mLocationClient.connect();
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        myDBHelper = SQLiteHelperHospitales.getInstance(this);
        tipoCentros = getIntent().getExtras().getInt("tipoCentros");
        idCentro = getIntent().getExtras().getInt("idCentro");
        tipoBusqueda = getIntent().getExtras().getInt("tipoBusqueda");
        prepararMapa(tipoCentros);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepararMapa(int tipo){
        String[][] centros = null;
        switch (tipo){
            case CERCANOS:
                break;
            case ESSALUD:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO: case UNOPORRED:
                        centros = myDBHelper.getCentro(idCentro);
                        for(int i=0;i<centros.length;i++){
                            id = Integer.parseInt(centros[i][0]);
                            nombre = centros[i][1];
                            latitud= centros[i][2];
                            longitud = centros[i][3];
                            direccion = centros[i][4];
                            telefono1 = centros[i][5];
                            telefono2 = centros[i][6];
                            coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                        moverCamara(coordenadas,ZOOM_CENTRO);
                        break;
                    case TODOSPORDEPARTAMENTO:
                        centros = myDBHelper.getCentrosEsSaludxDepartamento(1);
                        for(int i=0;i<centros.length;i++){
                            id = Integer.parseInt(centros[i][0]);
                            nombre = centros[i][1];
                            latitud= centros[i][2];
                            longitud = centros[i][3];
                            direccion = centros[i][4];
                            telefono1 = centros[i][5];
                            telefono2 = centros[i][6];
                            coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                        moverCamara(coordenadas,ZOOM_DEPARTAMENTO);
                        break;
                    case TODOSPORRED:
                        centros = myDBHelper.getCentrosEsSaludxRedes(idCentro);
                        for(int i=0;i<centros.length;i++){
                            id = Integer.parseInt(centros[i][0]);
                            nombre = centros[i][1];
                            latitud= centros[i][2];
                            longitud = centros[i][3];
                            direccion = centros[i][4];
                            telefono1 = centros[i][5];
                            telefono2 = centros[i][6];
                            coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                        moverCamara(coordenadas,ZOOM_DEPARTAMENTO);
                        break;
                }

                break;
            case MINSA:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO:
                        centros = myDBHelper.getCentrosEsSaludxDepartamento(1);
                        break;
                    case UNOPORRED:
                        centros = myDBHelper.getCentrosEsSaludxRedes(1);
                        break;
                }
                for(int i=0;i<centros.length;i++){
                    id = Integer.parseInt(centros[i][0]);
                    nombre = centros[i][1];
                    latitud= centros[i][2];
                    longitud = centros[i][3];
                    direccion = centros[i][4];
                    telefono1 = centros[i][5];
                    telefono2 = centros[i][6];
                    coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                    map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
                break;
            case FFAAPP:
                break;
            case SISOL:
                break;
            case PRIVADOS:
                break;
        }
    }

    private void moverCamara(LatLng coord, int zoom) {
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(coord, zoom);
        map.animateCamera(cameraUpdate);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(isGooglePlayServicesAvailable()){
            mLocationClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS==resultCode){
            return true;
        }
        else{
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if(errorDialog!=null){
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(),"Location updates");
            }
            return false;
        }
    }

    @Override
    protected void onStop(){
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        String[][] centros;
        currentLocation = mLocationClient.getLastLocation();
        if(currentLocation!=null){
            misCoordenadas = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            if(tipoCentros==CERCANOS){
                map.addMarker(new MarkerOptions().position(misCoordenadas).title("Aquí estoy").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                centros = myDBHelper.getCentrosCercanos(misCoordenadas.latitude, misCoordenadas.longitude);
                for(int i=0;i<centros.length;i++){
                    id = Integer.parseInt(centros[i][0]);
                    nombre = centros[i][1];
                    latitud= centros[i][2];
                    longitud = centros[i][3];
                    direccion = centros[i][4];
                    telefono1 = centros[i][5];
                    telefono2 = centros[i][6];
                    institucion = Integer.parseInt(centros[i][7]);
                    coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                    switch (institucion){
                        case ESSALUD:
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            break;
                        case MINSA:
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            break;
                        case FFAAPP:
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            break;
                        case PRIVADOS:
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            break;
                        case SISOL:
                            map.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                            break;
                    }

                }
                moverCamara(misCoordenadas,ZOOM_CERCANOS);
            }
        }
        else {

        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException ex){
                ex.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "Location change", Toast.LENGTH_LONG).show();
        misCoordenadas = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}