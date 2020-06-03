import java.util.function.Consumer;

// LLFS a.k.a LowLevelFileSystem
public class ArchivoLLFS implements Archivo {
    private LowLevelFileSystem fileSystem;
    private String path;
    private int id;
    private boolean finDeArchivoLLFS = false;

    public ArchivoLLFS(LowLevelFileSystem fileSystem, String path) {
        this.path = path;
        this.fileSystem = fileSystem;
    }

    public ArchivoLLFS escrituraSincronica(byte[] bloque, int posicionInicial, int longitudBloque) {
        fileSystem.syncWriteFile(id, bloque, posicionInicial, posicionInicial + longitudBloque - 1);
        return this;
    }

    // Lectura sincrónica 1: Call and return
    public byte[] lecturaSincronica(int posicionInicial, int longitudBloque) {
        byte[] bloque = new byte[longitudBloque];
        int cantBytesLeidos = fileSystem.syncReadFile(id, bloque, posicionInicial,
                posicionInicial + longitudBloque - 1);
        finalizarLectura(cantBytesLeidos, longitudBloque);
        return bloque;
    }

    // Lectura sincrónica 2: Memoria compartida
    public ArchivoLLFS lecturaSincronica(byte[] bloque, int posicionInicial, int longitudBloque) {
        int cantBytesLeidos = fileSystem.syncReadFile(id, bloque, posicionInicial, posicionInicial + longitudBloque - 1);
        this.finalizarLectura(cantBytesLeidos, longitudBloque);
        return this;
    }

    // Lectura asincrónica 1: Continuaciones
    public ArchivoLLFS lecturaAsincronica(byte[] bloque, int posicionInicial, int longitudBloque,
            Consumer<byte[]> callback) {
        fileSystem.asyncReadFile(id, bloque, posicionInicial, posicionInicial + longitudBloque - 1,
                callbackLecturaAsincronica(callback, bloque, longitudBloque));
        return this;
    }

    private Consumer<Integer> callbackLecturaAsincronica(Consumer<byte[]> callback, byte[] bloque, int longitudBloque) {
        return cantBytesLeidos -> {
            this.finalizarLectura(cantBytesLeidos, longitudBloque);
            callback.accept(bloque);
        };
    }

    // Lectura asincrónica 2: Stream
    public StreamArchivo streamLimited(int cantidadBloques, int longitudBloque) {
        return new StreamArchivoLimited(this, longitudBloque, cantidadBloques);
    }

    public StreamArchivo streamInfinite(int longitudBloque) {
        return new StreamArchivoInfinite(this, longitudBloque);
    }

    private void finalizarLectura(int cantBytesLeidos, int longitudBloque) {
        this.verificarExisteArchivoLLFS(cantBytesLeidos);
        this.actualizarFinDeArchivoLLFS(cantBytesLeidos, longitudBloque);
    }

    private void verificarExisteArchivoLLFS(int cantBytesLeidos) {
        if (cantBytesLeidos < 0)
            throw new RuntimeException("No se pudo leer el ArchivoLLFS" + path);
    }

    private void actualizarFinDeArchivoLLFS(int cantBytesLeidos, int longitudBloque) {
        if (cantBytesLeidos < longitudBloque)
            finDeArchivoLLFS = true;
    }

    public ArchivoLLFS abrir() {
        id = fileSystem.openFile(path);
        return this;
    }

    public ArchivoLLFS cerrar() {
        fileSystem.closeFile(id);
        return this;
    }

    public boolean finDeArchivo() {
        return finDeArchivoLLFS;
    }
}