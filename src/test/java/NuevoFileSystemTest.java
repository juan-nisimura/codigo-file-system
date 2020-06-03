import java.util.function.*;

import org.junit.*;

public class NuevoFileSystemTest {
    Predicate<byte[]> unPredicado;
    Function<byte[],byte[]> unaOperacion;
    Consumer<byte[]> haceAlgo, unaContinuacion;
    String UN_NOMBRE = "unArchivo", OTRO_NOMBRE = "otroArchivo";
    int LONGITUD_BLOQUE = 10, CANTIDAD_BLOQUES = 5;
    LowLevelFileSystem fileSystemMock;
    Archivo unArchivo, otroArchivo;
    byte[] c0, c1, c2, unBloque;

    @Before
    public void Fixture() {
        unArchivo = new ArchivoLLFS(fileSystemMock, UN_NOMBRE);
        otroArchivo = new ArchivoLLFS(fileSystemMock, OTRO_NOMBRE);
    }

    // TODO Los tests no testean nada, solo muestran formas de aplicar la nueva interfaz

    @Test
    public void lecturaYEscrituraCallAndReturn() {
        c0 = unArchivo.lecturaSincronica(0, 4);
        c1 = unArchivo.lecturaSincronica(0, 1);
        c2 = unArchivo.lecturaSincronica(0, 5);
        otroArchivo.escrituraSincronica(c0, 0, 4)
            .escrituraSincronica(new byte[] { 0x0, 0x10, 0x0 }, 0, 3)
            .escrituraSincronica(c1, 0, 1)
            .escrituraSincronica(c2, 0, 5);
    }

    @Test
    public void lecturaYEscrituraSincronicaMemoriaCompartida() {
        unArchivo.lecturaSincronica(c0, 0, 4)
            .lecturaSincronica(c1, 0, 1)
            .lecturaSincronica(c2, 0, 5);
        otroArchivo.escrituraSincronica(c0, 0, 4)
            .escrituraSincronica(new byte[] { 0x0, 0x10, 0x0 }, 0, 3)
            .escrituraSincronica(c1, 0, 1)
            .escrituraSincronica(c2, 0, 5);
    }

    @Test
    public void ejemploLecturaAsincronicaConContinuacion() {
        unArchivo.lecturaAsincronica(unBloque, 0, LONGITUD_BLOQUE, unaContinuacion);
    }

    @Test
    public void copiarArchivoSincronicamente() {
        while (!unArchivo.finDeArchivo()) {
            unArchivo.lecturaSincronica(unBloque, 0, LONGITUD_BLOQUE);
            unArchivo.escrituraSincronica(unBloque, 0, LONGITUD_BLOQUE);
        }
    }

    @Test
    public void copiarArchivoConStream() {
        unArchivo.streamInfinite(LONGITUD_BLOQUE)
            .forEach(bloque -> otroArchivo.escrituraSincronica(bloque, 0, LONGITUD_BLOQUE))
            .suscribe();
    }

    @Test
    public void otroEjemploConStream() {
        unArchivo.streamLimited(CANTIDAD_BLOQUES,LONGITUD_BLOQUE)
            .filter(unPredicado)
            .map(unaOperacion)
            .forEach(haceAlgo)
            .suscribe();
    }
}
