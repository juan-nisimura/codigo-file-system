import java.util.function.Consumer;

public interface LowLevelFileSystem {
    int openFile(String path);

    void closeFile(int fd);

    int syncReadFile(int fd, byte[] bufferBytes, int bufferStart, int bufferEnd);

    void syncWriteFile(int fd, byte[] bufferBytes, int bufferStart, int bufferEnd);

    void asyncReadFile(int fd, byte[] buffer, int bufferStart, int bufferEnd, Consumer<Integer> callback);
}