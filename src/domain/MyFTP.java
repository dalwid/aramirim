package domain;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Scanner;



public class MyFTP {

    private static FTPClient ftpClient = new FTPClient();
    private static Scanner scanner = new Scanner(System.in);

    public static void menu() {
        connectToServer();

        boolean running = true;
        while (running)

        {
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



private static void connectToServer() {
    try {
        // Configurações do InfinityFree
        String server = "ftpupload.net";
        int port = 21;
        String user = "if0_39307185"; // Substitua pelo seu usuário
        String pass = "7ZE1ZXUtcS9";   // Substitua pela sua senha

        ftpClient.connect(server, port);
        int replyCode = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(replyCode)) {
            System.out.println("Conexão falhou. Código: " + replyCode);
            return;
        }

        boolean success = ftpClient.login(user, pass);
        if (!success) {
            System.out.println("Login falhou. Verifique usuário e senha.");
            return;
        }

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        System.out.println("Conectado com sucesso ao InfinityFree! Quack!");

        // Mudar para diretório htdocs
        ftpClient.changeWorkingDirectory("htdocs");

    } catch (IOException e) {
        System.out.println("Erro na conexão: " + e.getMessage());
    }
}

private static void listFiles() {
    try {
        System.out.println("\nArquivos no servidor:");
        FTPFile[] files = ftpClient.listFiles();

        for (FTPFile file : files) {
            String details = file.isDirectory() ? "[DIR] " : "[FILE]";
            details += " " + file.getName();
            details += " (" + file.getSize() + " bytes)";
            System.out.println(details);
        }
    } catch (IOException e) {
        System.out.println("Erro ao listar arquivos: " + e.getMessage());
    }
}

private static void uploadFileOrDirectory() {
    System.out.print("\nDigite o caminho local do arquivo/pasta: ");
    String localPath = scanner.nextLine();

    File localFile = new File(localPath);
    if (!localFile.exists()) {
        System.out.println("Arquivo/pasta não encontrado!");
        return;
    }

    try {
        if (localFile.isDirectory()) {
            uploadDirectory(localFile, "");
        } else {
            uploadSingleFile(localFile);
        }
        System.out.println("Upload completo! Quack!");
    } catch (IOException e) {
        System.out.println("Erro no upload: " + e.getMessage());
    }
}

private static void uploadSingleFile(File localFile) throws IOException {
    try (InputStream inputStream = new FileInputStream(localFile)) {
        boolean success = ftpClient.storeFile(localFile.getName(), inputStream);
        if (!success) {
            System.out.println("Falha ao enviar: " + localFile.getName());
        }
    }
}

private static void uploadDirectory(File localDir, String remoteDirPath) throws IOException {
    ftpClient.makeDirectory(remoteDirPath + localDir.getName());
    String newRemoteDir = remoteDirPath + localDir.getName() + "/";

    File[] files = localDir.listFiles();
    if (files != null) {
        for (File file : files) {
            if (file.isDirectory()) {
                uploadDirectory(file, newRemoteDir);
            } else {
                uploadSingleFileToPath(file, newRemoteDir);
            }
        }
    }
}

private static void uploadSingleFileToPath(File localFile, String remotePath) throws IOException {
    try (InputStream inputStream = new FileInputStream(localFile)) {
        boolean success = ftpClient.storeFile(remotePath + localFile.getName(), inputStream);
        if (!success) {
            System.out.println("Falha ao enviar: " + remotePath + localFile.getName());
        }
    }
}

private static void downloadFile() {
    try {
        listFiles();
        System.out.print("\nDigite o nome do arquivo para baixar: ");
        String remoteFile = scanner.nextLine();

        System.out.print("Digite o caminho local para salvar: ");
        String localPath = scanner.nextLine();

        File localFile = new File(localPath);
        try (OutputStream outputStream = new FileOutputStream(localFile)) {
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
            if (success) {
                System.out.println("Download completo! Quack!");
            } else {
                System.out.println("Falha no download!");
            }
        }
    } catch (IOException e) {
        System.out.println("Erro no download: " + e.getMessage());
    }
}

private static void disconnect() {
    try {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("Desconectado do servidor FTP");
        }
    } catch (IOException e) {
        System.out.println("Erro ao desconectar: " + e.getMessage());
    }
}
}
