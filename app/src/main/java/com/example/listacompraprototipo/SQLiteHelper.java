package com.example.listacompraprototipo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.listacompraprototipo.model.Categoria;
import com.example.listacompraprototipo.model.ListaCompra;
import com.example.listacompraprototipo.model.Producto;
import com.example.listacompraprototipo.model.ProductoLista;

import java.util.ArrayList;

/**
 * SqlHelper, con esta clase vamos a cargar y controlar todos los datos de nuestra aplicacion.
 * todos los datos persistentes y los datos comunes entre las diversas activities de nuestro
 * proyecto son tratados aquí.
 * Para ello aplicamos singleton.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    //Singleton
    private static SQLiteHelper sqlHelper=null;
    public static SQLiteHelper getInstance(Context context){
        if(sqlHelper==null){
            sqlHelper=new SQLiteHelper(context);
        }
        return sqlHelper;
    }
    //Sql things
    private static final String dbName="ListaCompra.db";
    private static final int dbVersion=1;
    private static final String CREATE_CATEGORIAS ="CREATE TABLE Categorias (nombre STRING (30) PRIMARY KEY, imagen INTEGER);";
    private static final String CREATE_PRODUCTOS ="CREATE TABLE Productos (nombre STRING PRIMARY KEY, categoria STRING REFERENCES Categorias (Nombre), image INTEGER);";
    private static final String CREATE_LISTAS ="CREATE TABLE ListasCompra (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre STRING);";
    private static final String CREATE_ITEM_LISTA ="CREATE TABLE ItemsListaCompra (id INTEGER PRIMARY KEY , producto STRING REFERENCES Productos (nombre), idLista INTEGER REFERENCES ListasCompra (id) ON DELETE CASCADE ON UPDATE CASCADE, cantidad INTEGER, comprado BOOLEAN);";
    private static final String[] INSERT_CATEGORIAS ={"INSERT INTO Categorias (Nombre, imagen) VALUES ('Quesos', 0x1F9C0 );" ,
            "INSERT INTO Categorias (Nombre, imagen) VALUES ('Carnes y aves',0x1F357 );" ,
            "INSERT INTO Categorias (Nombre, imagen) VALUES ('Frutas y vegetales', 0x1F34E);" ,
            "INSERT INTO Categorias (Nombre, imagen) VALUES ('Otros', 0x1F3F7);" ,
            "INSERT INTO Categorias (Nombre, imagen) VALUES ('Pescado', 0x1F41F);"};

    private static final String[] INSERT_PRODUCTOS ={"INSERT INTO Productos (nombre, categoria, image) VALUES ('Emperador', 'Pescado', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Ternera', 'Carnes y aves', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Queso curado', 'Quesos', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Limones', 'Frutas y vegetales', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Atun', 'Pescado', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Pollo', 'Carnes y aves', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Queso de cabra', 'Quesos', NULL);" ,
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Tomates', 'Frutas y vegetales', NULL);",
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Fuego Facil', 'Otros', NULL);",
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Naranjas', 'Frutas y vegetales', NULL);",
            "INSERT INTO Productos (nombre, categoria, image) VALUES ('Pasta de Dientes', 'Otros', NULL);"
    };
    private static final String INSERT_LISTAS ="INSERT INTO ListasCompra (id, nombre) VALUES (1, 'Mi Primera Lista');";
    private static final String[] INSERT_ITEM_LISTAS={"INSERT INTO ItemsListaCompra (id, producto, idLista, cantidad, comprado) VALUES (1, 'Tomates', 1, 1, 'true');" ,
            "INSERT INTO ItemsListaCompra (id, producto, idLista, cantidad, comprado) VALUES (2, 'Ternera', 1, 2, 'false');"};
    private SQLiteHelper(@Nullable Context context
                         ) {
        super(context, dbName, null,dbVersion);
    }

    private ArrayList<Categoria> categorias;
    private ArrayList<Producto> productos;
    private ArrayList<ListaCompra> listas;


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CATEGORIAS);
        db.execSQL(CREATE_PRODUCTOS);
        db.execSQL(CREATE_LISTAS);
        db.execSQL(CREATE_ITEM_LISTA);

        for (String cat:INSERT_CATEGORIAS
             ) {
            db.execSQL(cat);
        }
        for (String prod:INSERT_PRODUCTOS
             ) {
            db.execSQL(prod);
        }
        db.execSQL(INSERT_LISTAS);
        for (String itemList: INSERT_ITEM_LISTAS
             ) {
            db.execSQL(itemList);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Funcion principal de carga de datos desde la SQLiteBD
     * importa el orden en la carga para poder construir correctamente todos los objetos
     * @return
     */
    public boolean cargarDatos(){
        SQLiteDatabase db= this.getReadableDatabase();
        //Cargar categorias
        categorias=new ArrayList<>();

        Cursor c=db.rawQuery("SELECT nombre,imagen FROM Categorias;",null);
        if(c.moveToFirst()){
            do{
                String s= c.getString(c.getColumnIndex("nombre"));
                int imagen=c.getInt(c.getColumnIndex("imagen"));
                categorias.add(new Categoria(s,imagen));
            }while (c.moveToNext());
        }
        c.close();
        //Productos
        productos=new ArrayList<>();

        c=db.rawQuery("SELECT nombre,categoria FROM Productos",null);
        if(c.moveToFirst()){
            do{
                String nombreProd= c.getString(c.getColumnIndex("nombre"));
                String catProd=c.getString(c.getColumnIndex("categoria"));
                Categoria categoriaProducto=null;
                for (Categoria feCategory:categorias
                     ) {
                    if(feCategory.getNombre().equals(catProd)){
                        categoriaProducto=feCategory;
                    }
                }
                productos.add(new Producto(nombreProd,categoriaProducto));
            }while (c.moveToNext());
        }
        c.close();

        //Listas
        listas=new ArrayList<>();

        c=db.rawQuery("SELECT id,nombre FROM ListasCompra",null);
        if(c.moveToFirst()){
            do{
                int idLista=c.getInt(0);
                String nomLista=c.getString(1);
                listas.add(new ListaCompra(idLista,nomLista));
            }while (c.moveToNext());
        }
        c.close();

        //productos lista

        for (ListaCompra feLista:listas
             ) {
            String[] argumentosListas={String.valueOf(feLista.getId())};
            c=db.rawQuery("SELECT id, producto, idLista, cantidad, comprado FROM ItemsListaCompra where idLista=?",argumentosListas);
            if (c.moveToFirst()){
                do {
                    int idProdLista=c.getInt(0);
                    String nombreProductoLista=c.getString(1);
                    int cantidadProducto= c.getInt(3);
                    boolean comprado=c.getInt(4)>0;
                    Producto p= null;
                    for (Producto feProd:productos
                         ) {
                        if(nombreProductoLista.equals(feProd.getNombre())){
                            p=feProd;
                        }
                    }
                    if (p!=null) {
                        feLista.addProducto(new ProductoLista(idProdLista, p, cantidadProducto, comprado));
                    }
                }while (c.moveToNext());
            }
        }
        db.close();
        return true;
    }


    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public ArrayList<ListaCompra> getListas() {
        return listas;
    }

    /**
     * Esta funcion comprueba que el proudcto esta en la lista. Si esta, añade uno a su valor
     * si no lo encuentra crea un nuevo producto lista y lo anyade a las dos bases de datos
     * @param producto
     * @param listaCompra
     */
    public void addProductoLista(Producto producto, ListaCompra listaCompra){
        boolean encontrado=false;
        ProductoLista productoLista=null;

       int contador=0;
       while (!encontrado && contador<listaCompra.getProductos().size()){

           ProductoLista pl=listaCompra.getProductos().get(contador);
           if(pl.getNombre().equals(producto.getNombre())){
               productoLista=pl;
               encontrado=true;
           }
           contador++;
       }
        SQLiteDatabase db=getWritableDatabase();
       if(encontrado){
           //update
           modificarCantidadProductoDB(productoLista,1);
       }else {
           productoLista=new ProductoLista(producto);

           ContentValues nuevoItem=new ContentValues();
           nuevoItem.put("id",productoLista.getId());
           nuevoItem.put("producto",productoLista.getNombre());
           nuevoItem.put("idLista",listaCompra.getId());
           nuevoItem.put("cantidad",productoLista.getCantidad());
           nuevoItem.put("comprado",productoLista.isComprado());
           db.insert("ItemsListaCompra",null,nuevoItem);
           listaCompra.addProducto(productoLista);
           db.close();
       }


    }

    /**
     * Esta funcion modifica actualiza la cantidad del producto en la lista especificada
     * solo se usa para sumar 1 con lo que sera refactorizada en breves.
     * @param productoLista
     * @param sumaResta
     */
    public void modificarCantidadProductoDB(ProductoLista productoLista,int sumaResta){
        productoLista.setCantidad(productoLista.getCantidad()+sumaResta);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values =new ContentValues();
        String[] args={String.valueOf(productoLista.getId())};
        values.put("cantidad",productoLista.getCantidad());
        db.update("ItemsListaCompra",values,"id=?",args);
        db.close();

    }


    /**
     * Esta funcion asigna valor al booleano comprado de la base de datos.
     * Actua sobre la tabla ItemsListaCompra
     * @param productoLista
     * @param marcar
     */
    public void marcarDesmarcarProducto(ProductoLista productoLista,boolean marcar){
        productoLista.setComprado(marcar);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values =new ContentValues();
        String[] args={String.valueOf(productoLista.getId())};
        values.put("comprado",productoLista.isComprado());
        db.update("ItemsListaCompra",values,"id=?",args);
        db.close();

    }

    public ArrayList<Producto> getProductosFromCategoria(Categoria categoria){

        return null;
    }

    public void removeProducto(ProductoLista p,ListaCompra listaCompra){

        ProductoLista productoLista=null;
        for (ProductoLista pl:listaCompra.getProductos()
             ) {
            if(pl.getNombre().equals(p.getNombre())&&
                    pl.getCategoria().equals(p.getCategoria())){
                productoLista=pl;

            }
        }


        String[] args={String.valueOf(productoLista.getId())};
        SQLiteDatabase db=getWritableDatabase();

        //caso update
        if(productoLista.getCantidad()>1){
                productoLista.setCantidad(productoLista.getCantidad()-1);
                p.setCantidad(productoLista.getCantidad());
            ContentValues values =new ContentValues();
            values.put("cantidad",productoLista.getCantidad());
            db.update("ItemsListaCompra",values,"id=?",args);

        //caso remove
        } else if( productoLista.getCantidad()==1){
            productoLista.setCantidad(0);
            listaCompra.removeProducto(productoLista);
            db.delete("ItemsListaCompra","id=?",args);
            p.setCantidad(0);

        }

        db.close();
    }



}
