import java.util.function.Consumer;

public interface Archivo {

    public Archivo escrituraSincronica(byte[] bloque, int posicionInicial, int longitudBloque);

    public byte[] lecturaSincronica(int posicionInicial, int longitudBloque);

    public Archivo lecturaSincronica(byte[] bloque, int posicionInicial, int longitudBloque);

    public Archivo lecturaAsincronica(byte[] bloque, int posicionInicial, int longitudBloque, Consumer<byte[]> callback);

    public StreamArchivo streamLimited(int cantidadBloques, int longitudBloque);

    public StreamArchivo streamInfinite(int longitudBloque);
    
    public Archivo abrir();

    public Archivo cerrar();

    public boolean finDeArchivo();
}
