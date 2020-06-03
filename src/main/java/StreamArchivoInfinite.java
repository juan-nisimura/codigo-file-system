

public class StreamArchivoInfinite extends StreamArchivo {

    public StreamArchivoInfinite(Archivo archivo, int longitudBloque) {
        this.archivo = archivo;
        this.longitudBloque = longitudBloque;
    }

    @Override
    public boolean condicionFinDeLectura() {
        return archivo.finDeArchivo();
    }
}
