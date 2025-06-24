package domain;

import java.util.Scanner;

import static domain.config.ServerSide.connectToServer;
import static domain.config.ServerSide.disconnect;
import static domain.files.Files.*;


public class Menu {
    private static Scanner scanner = new Scanner(System.in);

    public static void menu() {
        // Configurações do InfinityFree
        String host = "ftpupload.net";
        String user = "if0_39307185";
        String password = "7ZE1ZXUtcS9";
        int port = 21;

        connectToServer(host, port, user, password);

        boolean running = true;
        while (running) {
            System.out.println("\n=== MENU FTP ===");
            System.out.println("1. Listar arquivos");
            System.out.println("2. Enviar arquivo/pasta");
            System.out.println("3. Baixar arquivo");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (choice) {
                case 1:
                    listFiles();
                    break;
                case 2:
                    uploadFileOrDirectory();
                    break;
                case 3:
                    downloadFile();
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida! Quack!");
            }
        }

        disconnect();
    }
}
