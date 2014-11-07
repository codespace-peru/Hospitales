package pe.com.codespace.hospitales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;


public class ActivityMain extends Activity {
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

    private List<Tools.RowParent> listHeader;
    private HashMap<Tools.RowParent, List<Tools.RowChild>> listChild;
    SQLiteHelperHospitales myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        final ExpandableListView myExpand = (ExpandableListView) findViewById(R.id.explvMain);
        myDBHelper = SQLiteHelperHospitales.getInstance(this);
        prepararData();
        AdapterExpandableList myAdapter = new AdapterExpandableList(this,listHeader,listChild);
        myExpand.setAdapter(myAdapter);
        myExpand.setGroupIndicator(null);

        myExpand.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int pos) {
                if(lastExpandedPosition != -1 && pos != lastExpandedPosition)
                    myExpand.collapseGroup(lastExpandedPosition);
                lastExpandedPosition = pos;
            }
        });

        myExpand.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int pos, long l) {
                Intent intentMapa = new Intent(view.getContext(), ActivityMap.class);
                Intent intentCentros = new Intent(view.getContext(), ActivityCentros.class);
                //String idChild = ((TextView) view.findViewById(R.id.tvIdChild)).getText().toString();
                //String nameChild = ((TextView) view.findViewById(R.id.tvNameChild)).getText().toString();
                switch (pos+1){
                    case CERCANOS:
                        intentMapa.putExtra("tipoCentros",CERCANOS);
                        startActivity(intentMapa);
                        break;
                    case ESSALUD: case MINSA:
                        break;
                    case PRIVADOS: case FFAAPP: case SISOL:
                        intentCentros.putExtra("tipoCentros",pos+1);
                        intentCentros.putExtra("red_departamento",UNOPORDEPARTAMENTO);
                        startActivity(intentCentros);
                        break;
                }
                return false;
            }
        });

        myExpand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Intent intentCentros = new Intent(view.getContext(), ActivityCentros.class);
                //String idItem = ((TextView) view.findViewById(R.id.tvIdChild)).getText().toString();
                //String nameChild = ((TextView) view.findViewById(R.id.tvNameChild)).getText().toString();
                intentCentros.putExtra("tipoCentros",groupPosition+1);
                switch (groupPosition+1){
                    case ESSALUD:
                        //intentCentros.putExtra("red_departamento",childPosition+1); // 1:departamentos; 2:redes
                        //startActivity(intentCentros);
                        switch (childPosition+1){
                            case UNOPORDEPARTAMENTO:
                                intentCentros.putExtra("tipoBusqueda",UNOPORDEPARTAMENTO);
                                startActivity(intentCentros);
                                break;
                            case UNOPORRED:
                                intentCentros.putExtra("tipoBusqueda",UNOPORRED);
                                startActivity(intentCentros);
                                break;
                            case TODOSPORDEPARTAMENTO:
                                break;
                            case TODOSPORRED:
                                intentCentros.putExtra("tipoBusqueda",TODOSPORRED);
                                startActivity(intentCentros);
                                break;
                        }
                        break;
                    case MINSA:

                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity__main, menu);
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

    private void prepararData(){
        listHeader = new ArrayList<Tools.RowParent>();
        listChild = new HashMap<Tools.RowParent, List<Tools.RowChild>>();
        List<Tools.RowChild> gruposList;
        //Agregamos los padres
        Tools.RowParent rowGroup;
        rowGroup = new Tools.RowParent(CERCANOS,"ESTABLECIMIENTOS CERCANOS");
        listHeader.add(rowGroup);
        rowGroup = new Tools.RowParent(ESSALUD,"ESSALUD");
        listHeader.add(rowGroup);
        rowGroup = new Tools.RowParent(MINSA,"MINISTERIO DE SALUD");
        listHeader.add(rowGroup);
        rowGroup = new Tools.RowParent(SISOL,"HOSPITALES DE LA SOLIDARIDAD");
        listHeader.add(rowGroup);
        rowGroup = new Tools.RowParent(PRIVADOS,"CLINICAS PRIVADAS");
        listHeader.add(rowGroup);
        rowGroup = new Tools.RowParent(FFAAPP,"FUERZAS ARMADAS Y POLICIALES");
        listHeader.add(rowGroup);

        listChild.put(listHeader.get(CERCANOS-1), new ArrayList<Tools.RowChild>());
        listChild.put(listHeader.get(SISOL-1), new ArrayList<Tools.RowChild>());
        listChild.put(listHeader.get(PRIVADOS-1), new ArrayList<Tools.RowChild>());
        listChild.put(listHeader.get(FFAAPP-1), new ArrayList<Tools.RowChild>());

        //Agregamos los hijos al segundo padre -ESSALUD-
        gruposList = new ArrayList<Tools.RowChild>();
        Tools.RowChild rowItem;
        rowItem = new Tools.RowChild(1,"Buscar un Establecimiento por Departamento");
        gruposList.add(rowItem);
        rowItem = new Tools.RowChild(2,"Buscar un Establecimiento por Red Asistencial");
        gruposList.add(rowItem);
        rowItem = new Tools.RowChild(3,"Ver los Establecimientos de un Departamento");
        gruposList.add(rowItem);
        rowItem = new Tools.RowChild(4,"Ver los Establecimientos de una Red Asistencial");
        gruposList.add(rowItem);
        listChild.put(listHeader.get(ESSALUD-1),gruposList);

        //Agregamos los hijos al tercer padre -MINSA-
        gruposList = new ArrayList<Tools.RowChild>();
        rowItem = new Tools.RowChild(1,"Buscar un Establecimiento por Departamento");
        gruposList.add(rowItem);
        rowItem = new Tools.RowChild(2,"Buscar un Establecimiento por Red Asistencial");
        gruposList.add(rowItem);
        rowItem = new Tools.RowChild(3,"Ver los Establecimientos de un Departamento");
        gruposList.add(rowItem);
        rowItem = new Tools.RowChild(4,"Ver los Establecimientos de una Red Asistencial");
        gruposList.add(rowItem);
        listChild.put(listHeader.get(MINSA-1),gruposList);
    }
}
