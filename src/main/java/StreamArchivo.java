import java.util.*;
import java.util.function.*;

public abstract class StreamArchivo {
    List<Function<byte[], byte[]>> operaciones = new ArrayList<Function<byte[], byte[]>>();
    Archivo archivo;
    int longitudBloque;

    public void suscribe(){
        archivo.lecturaAsincronica(new byte[longitudBloque], 0, longitudBloque - 1, continuacionSuscribe());
    }

    public Consumer<byte[]> continuacionSuscribe() {
        return unBloque -> {
            this.aplicarOperaciones(unBloque);
            if(!condicionFinDeLectura()) {
                archivo.lecturaAsincronica(new byte[longitudBloque], 0, longitudBloque - 1, this.continuacionSuscribe());
            }
        };
    }

    abstract public boolean condicionFinDeLectura();

    private void aplicarOperaciones(byte[] bloque) {
        Iterator<Function<byte[], byte[]>> iterador = operaciones.iterator();
        try{
            while(iterador.hasNext()){
                iterador.next().apply(bloque);
            }
        }
        catch(NoPasoElFiltroException exception){}
    }

    public StreamArchivo forEach(Consumer<byte[]> operacion) {
        Function<byte[],byte[]> nuevaOperacion = unObjeto -> {
            operacion.accept(unObjeto);
            return unObjeto;
        };
        operaciones.add(nuevaOperacion);
        return this;
    }

    public StreamArchivo map(Function<byte[], byte[]> operacion) {
        operaciones.add(operacion);
        return this;
    }

    public StreamArchivo filter(Predicate<byte[]> predicado) {
        Function<byte[],byte[]> operacion = unObjeto -> {
            if(!predicado.test(unObjeto))
                throw new NoPasoElFiltroException("No pasó el filtro");
            return unObjeto;
        };
        operaciones.add(operacion);
        return this;
    }
}