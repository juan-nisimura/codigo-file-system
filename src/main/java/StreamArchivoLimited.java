public class StreamArchivoLimited extends StreamArchivo {
    int cantidadBloques;
    int bloquesLeidos = 0;

    public StreamArchivoLimited(Archivo archivo, int longitudBloque, int cantidadBloques) {
        this.archivo = archivo;
        this.longitudBloque = longitudBloque;
        this.cantidadBloques = cantidadBloques;
        this.forEach(bloque -> bloquesLeidos++);
    }

    @Override
    public boolean condicionFinDeLectura() {
        return archivo.finDeArchivo() || bloquesLeidos >= cantidadBloques;
    }
}
