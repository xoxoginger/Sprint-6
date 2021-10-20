import ru.sber.filesystem.VFilesystem
import ru.sber.filesystem.VPath
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
class FileServer {

    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs     A proxy filesystem to serve files from. See the VFilesystem
     *               class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    @Throws(IOException::class)
    fun run(socket: ServerSocket, fs: VFilesystem) {

        /**
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        socket.use {
            while (true) {
                val s = it.accept()
                handle(s, fs)
            }
        }
    }
    
    private fun handle(s: Socket, fs: VFilesystem) {
        s.use {
            val br = s.getInputStream().bufferedReader()
            val bw = s.getOutputStream().bufferedWriter()
            val request = br.readLine().split(" ")
            val requestType = request[0].uppercase()
            val requestPath = request[1]

            if (requestType == "GET") {
                if (fs.readFile(VPath(requestPath)) != null) {
                    bw.write("HTTP/1.0 200 OK\r\n Server: FileServer\r\n\r\n" + fs.readFile(VPath(requestPath)))
                    bw.flush()
                } 
                else {
                    bw.write("HTTP/1.0 404 Not Found\r\n Server: FileServer\r\n\r\n")
                    bw.flush()
                }
            }
        }
    }
}
