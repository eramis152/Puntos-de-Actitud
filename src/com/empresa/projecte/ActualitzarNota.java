package src.com.empresa.projecte; 

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class ActualitzarNota {

    // Método para encontrar el archivo, usa WalkFileTree para buscarlo de forma recursiva
    public static String findFile(String startDir, String fileName) throws IOException {
        Path startPath = Paths.get(startDir);
        AtomicReference<String> foundFilePath = new AtomicReference<>(null);

        Files.walkFileTree(startPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            // En este método, si encuentra el archivo termina la búsqueda 
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().equals(fileName)) {
                    foundFilePath.set(file.toAbsolutePath().toString());
                    return FileVisitResult.TERMINATE; 
                }
                return FileVisitResult.CONTINUE;
            }

            // Estos métodos están para que en caso de no encontrarlo en una ruta lo siga buscando 
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        return foundFilePath.get();
    }

    // Este método sirve para modificar la nota, sus parámetros son la dirección del archivo, el nombre del alumno y la nueva nota
    public static void modificarNota(String filePath, String alumne, char novaNota) {
        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();

        // Lee todo el archivo y si encuentra el alumno, agarra lo último que se encuentra en esa fila, es decir, la nota y la cambia
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains(alumne)) {
                    line = line.substring(0, line.length() - 1) + novaNota; // Cambiar la última posición por la nueva nota
                }
                sb.append(line).append(System.lineSeparator());
            }

        } catch (IOException e) {
            System.out.println("Error leyendo el archivo: " + e.getMessage());
            return;
        }

        // Escribe el archivo de nuevo pero con la modificación
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(sb.toString());
            System.out.println("Nota modificada correctamente.");
        } catch (IOException e) {
            System.out.println("Error escribiendo en el archivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            String directory = "C:/";  
            String fileName = "NotasDAM2.txt";            

          
            System.out.print("Introduce el nombre del alumno siguiendo este esquema APELLIDO1 APELLIDO2 NOMBRE (Todo debe de estar en mayusculas y los apellidos deben ir antes que el nombre): ");
            String alumneModificar = scanner.nextLine();

            System.out.print("Introduce la nueva nota, solo se acepta el 0 al 9 (No me seas burro): ");
            char novaNota = scanner.nextLine().charAt(0); 

            // Llamada a encontrar el archivo
            String result = findFile(directory, fileName);
            
            // Comprueba si lo ha encontrado
            if (result != null) {
                System.out.println("Archivo encontrado en: " + result);
                // Guarda la ruta en una variable
                String filePath = result; 
                // Llamada a modificar el archivo
                modificarNota(filePath, alumneModificar, novaNota);
            } else {
                System.out.println("Archivo no encontrado.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close(); 
        }
    }
}
