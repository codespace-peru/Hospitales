package pe.com.codespace.hospitales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by Carlos on 17/02/14.
 */
public class AdapterListView extends ArrayAdapter {

    private final Context context;
    private final List<Tools.RowChild> values;
    SQLiteHelperHospitales myDBHelper;

    public AdapterListView(Context pContext, List<Tools.RowChild> pValues) {
        super(pContext, R.layout.listview_item, pValues);
        this.context = pContext;
        this.values = pValues;
        myDBHelper = SQLiteHelperHospitales.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        Tools.TextHolderChild holder;

        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item,null);
            holder = new Tools.TextHolderChild(view);
            view.setTag(holder);
        }
        else{
            holder = (Tools.TextHolderChild) view.getTag();
        }

        final Tools.RowChild arts = values.get(position);
        holder.myIdChild.setText(String.valueOf(arts.IdChild));
        holder.myNameChild.setText(String.valueOf(arts.nombre));
        view.setPadding(10,0,10,0);
        return view;
    }
}
