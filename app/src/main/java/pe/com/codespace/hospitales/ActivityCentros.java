package pe.com.codespace.hospitales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.com.codespace.hospitales.R;

public class ActivityCentros extends Activity {
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

    int tipoCentros, tipoBusqueda; //, red_departamento;
    private List<Tools.RowParent> listHeader;
    private HashMap<Tools.RowParent, List<Tools.RowChild>> listChild;
    SQLiteHelperHospitales myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_centros);
        tipoCentros = getIntent().getExtras().getInt("tipoCentros");
        tipoBusqueda = getIntent().getExtras().getInt("tipoBusqueda");
        //red_departamento = getIntent().getExtras().getInt("red_departamento");

        final ExpandableListView myExpand = (ExpandableListView) findViewById(R.id.explvCentros);
        myDBHelper = SQLiteHelperHospitales.getInstance(this);
        prepararData(tipoCentros);
        AdapterExpandableListCentros myAdapter = new AdapterExpandableListCentros(this,listHeader,listChild, tipoCentros);
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
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ActivityMap.class);
                String idGroup = ((TextView) view.findViewById(R.id.tvIdParent)).getText().toString();
                String nameGroup = ((TextView) view.findViewById(R.id.tvNameParent)).getText().toString();
                switch (tipoBusqueda){
                    case TODOSPORRED:
                        intent.putExtra("tipoCentros",tipoCentros);
                        intent.putExtra("idCentro",Integer.parseInt(idGroup));
                        intent.putExtra("nombreCentro",nameGroup);
                        intent.putExtra("tipoBusqueda",tipoBusqueda);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });

        myExpand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Intent intent = new Intent(view.getContext(), ActivityMap.class);
                String idChild = ((TextView) view.findViewById(R.id.tvIdChild)).getText().toString();
                String nameChild = ((TextView) view.findViewById(R.id.tvNameChild)).getText().toString();
                intent.putExtra("tipoCentros",tipoCentros);
                intent.putExtra("idCentro",Integer.parseInt(idChild));
                intent.putExtra("nombreCentro",nameChild);
                intent.putExtra("tipoBusqueda",tipoBusqueda);
                //intent.putExtra("red_departamento",red_departamento);
                startActivity(intent);
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_centros, menu);
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

    private void prepararData(int tipoCentros){
        listHeader = new ArrayList<Tools.RowParent>();
        listChild = new HashMap<Tools.RowParent, List<Tools.RowChild>>();
        List<Tools.RowChild> gruposList;
        String[][] departamentos, redes, centros;
        Tools.RowParent rowGroup;
        Tools.RowChild rowItem;

        switch (tipoCentros){
            case ESSALUD:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO:
                        //Agregamos los padres
                        departamentos = myDBHelper.getDepartamentos();
                        for(int i=0; i<departamentos.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(departamentos[i][0]), departamentos[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxDepartamento(Integer.parseInt(departamentos[i][0]));
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                    case UNOPORRED:
                        //Agregamos los padres
                        redes = myDBHelper.getRedesEsSalud();
                        for(int i=0; i<redes.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(redes[i][0]), redes[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxRedes(i+1);
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                    case TODOSPORRED:
                        redes = myDBHelper.getRedesEsSalud();
                        for(int i=0; i<redes.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(redes[i][0]), redes[i][1]);
                            listHeader.add(rowGroup);
                            listChild.put(listHeader.get(i),new ArrayList<Tools.RowChild>());
                        }

                        break;
                }
                break;
            case MINSA:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO:
                        //Agregamos los padres
                        departamentos = myDBHelper.getDepartamentos();
                        for(int i=0; i<departamentos.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(departamentos[i][0]), departamentos[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxDepartamento(1);
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                    case UNOPORRED:
                        //Agregamos los padres
                        redes = myDBHelper.getRedesEsSalud();
                        for(int i=0; i<redes.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(redes[i][0]), redes[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxRedes(1);
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                }
                break;
            case SISOL:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO:
                        //Agregamos los padres
                        departamentos = myDBHelper.getDepartamentos();
                        for(int i=0; i<departamentos.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(departamentos[i][0]), departamentos[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxDepartamento(1);
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                }
                break;
            case PRIVADOS:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO:
                        //Agregamos los padres
                        departamentos = myDBHelper.getDepartamentos();
                        for(int i=0; i<departamentos.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(departamentos[i][0]), departamentos[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxDepartamento(1);
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                }
                break;
            case FFAAPP:
                switch (tipoBusqueda){
                    case UNOPORDEPARTAMENTO:
                        //Agregamos los padres
                        departamentos = myDBHelper.getDepartamentos();
                        for(int i=0; i<departamentos.length;i++){
                            rowGroup = new Tools.RowParent(Integer.parseInt(departamentos[i][0]), departamentos[i][1]);
                            listHeader.add(rowGroup);
                            //Agregamos los hijos
                            centros = myDBHelper.getCentrosEsSaludxDepartamento(1);
                            gruposList = new ArrayList<Tools.RowChild>();
                            for(int j=0; j<centros.length;j++){
                                rowItem = new Tools.RowChild(Integer.parseInt(centros[j][0]), centros[j][1]);
                                gruposList.add(rowItem);
                            }
                            listChild.put(listHeader.get(i),gruposList);
                        }
                        break;
                }
                break;
        }
    }
}
