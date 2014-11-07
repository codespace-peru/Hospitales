package pe.com.codespace.hospitales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 23/11/13.
 */
public class AdapterExpandableListCentros extends BaseExpandableListAdapter {
    private Context context;
    private List<Tools.RowParent> _listHeader;
    private HashMap<Tools.RowParent, List<Tools.RowChild>> _listChild;
    private int tipoCentro;

    private static final int CERCANOS = 1;
    private static final int ESSALUD = 2;
    private static final int MINSA = 3;
    private static final int SISOL = 4;
    private static final int PRIVADOS = 5;
    private static final int FFAAPP = 6;

    public AdapterExpandableListCentros(Context context, List<Tools.RowParent> listHeader, HashMap<Tools.RowParent, List<Tools.RowChild>> listChild, int tipoCentro){
        this.context = context;
        this._listHeader = listHeader;
        this._listChild = listChild;
        this.tipoCentro = tipoCentro;
    }


    @Override
    public int getGroupCount() {
        return this._listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listChild.get(this._listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listChild.get(this._listHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        View row = view;
        Tools.TextHolderParent holder;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.explistview_parent2, viewGroup, false);
            holder = new Tools.TextHolderParent(row);
            row.setTag(holder);
        }
        else{
            holder = (Tools.TextHolderParent) row.getTag();
        }
        switch (tipoCentro){
            case ESSALUD:
                row.setBackgroundColor(context.getResources().getColor(R.color.coloressalud));
                break;
            case MINSA:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorminsa));
                break;
            case SISOL:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorsisol));
                break;
            case PRIVADOS:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorprivado));
                break;
            case FFAAPP:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorffaapp));
                break;
        }
        Tools.RowParent temp = (Tools.RowParent) getGroup(groupPosition);
        holder.myIdParent.setText(String.valueOf(temp.IdParent));
        holder.myNameParent.setText(temp.nombre);
        return row;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        View row = view;
        Tools.TextHolderChild holder;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.explistview_child2, viewGroup, false);
            holder = new Tools.TextHolderChild(row);
            row.setTag(holder);
        }
        else {
            holder = (Tools.TextHolderChild) row.getTag();
        }

        switch (tipoCentro){
            case ESSALUD:
                row.setBackgroundColor(context.getResources().getColor(R.color.coloressalud));
                break;
            case MINSA:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorminsa));
                break;
            case SISOL:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorsisol));
                break;
            case PRIVADOS:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorprivado));
                break;
            case FFAAPP:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorffaapp));
                break;
        }

        Tools.RowChild temp = (Tools.RowChild) getChild(groupPosition, childPosition);
        if(temp != null){
            holder.myIdChild.setText(String.valueOf(temp.IdChild));
            holder.myNameChild.setText(temp.nombre);
        }
        row.setPadding(10,0,10,0);
        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}




