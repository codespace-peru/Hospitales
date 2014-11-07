package pe.com.codespace.hospitales;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by Carlos on 01/03/14.
 */
public class Tools {

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }


    public static class RowParent {
        int IdParent;
        String nombre;
        RowParent(int id, String nombre){
            this.IdParent = id;
            this.nombre = nombre;
        }
    }

    public static class RowChild {
        int IdChild;
        String nombre;
        RowChild(int id, String nombre){
            this.IdChild = id;
            this.nombre = nombre;
        }
    }

    public static class TextHolderParent {
        TextView myIdParent;
        TextView myNameParent;
        TextHolderParent(View v)
        {
            myIdParent = (TextView) v.findViewById(R.id.tvIdParent);
            myNameParent = (TextView) v.findViewById(R.id.tvNameParent);
        }
    }

    public static class TextHolderChild {
        TextView myIdChild;
        TextView myNameChild;
        TextHolderChild(View v)
        {
            myIdChild = (TextView) v.findViewById(R.id.tvIdChild);
            myNameChild = (TextView) v.findViewById(R.id.tvNameChild);
        }
    }


    public boolean isGPSAvailable(Context context){
        LocationManager locManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return false;
        else
            return true;
    }

}