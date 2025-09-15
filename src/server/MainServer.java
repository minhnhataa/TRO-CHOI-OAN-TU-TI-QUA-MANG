package server;

import common.Room;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainServer {
    public static void main(String[] args) {
        System.out.println(">>> MainServer starting...");
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("RPS Server listening on port 5000");

            RoomDAO rdao = new RoomDAO();
            // đảm bảo có phòng trong DB (nếu bạn chưa seed)
            rdao.ensureDefaultRooms();
            List<Room> rooms = rdao.listAll();

            RoomManager manager = new RoomManager();
            manager.loadRoomsFromDB(rooms);
            System.out.println("Loaded " + rooms.size() + " rooms from DB");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, manager)).start();
            }
        } catch (Exception e) {
            System.err.println("MainServer error:");
            e.printStackTrace(); // nhìn vào Console để biết lỗi gì
        }
    }
}
