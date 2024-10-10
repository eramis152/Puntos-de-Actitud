package test;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicReference;

public class ActualitzarNotaTest {

    //Metodo para encontrar el archivo, usa WalkFileTree Para buscarlo de forma recursiva usando como imput una ruta de inicio y el nombrte del archivo
    public static String findFile(String startDir, String fileName) throws IOException {
        Path startPath = Paths.get(startDir);
        AtomicReference<String> foundFilePath = new AtomicReference<>(null);

        Files.walkFileTree(startPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }
            //En este metodo si encuentra el archivo termina la busqueda 
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().equals(fileName)) {
                    foundFilePath.set(file.toAbsolutePath().toString());
                    return FileVisitResult.TERMINATE; 
                }
                return FileVisitResult.CONTINUE;
            }
            //Estos metodos estan para que en caso de no encontrarlo en una ruta lo siga buscando 
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
    // Este metodo sirve para modificar la nota, sus parametros son la direccion del archivo, el nombre del alumno y la nueva nota
    public static void modificarNota(String filePath, String alumne, char novaNota) {
        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();
        //Lee todo el archivo y si encuentra el alumno agarra lo ultimo que se encuentra en esa fila es decir la nota y la cambia
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains(alumne)) {
                    line = line.substring(0, line.length() - 1) + novaNota;
                }
                sb.append(line).append(System.lineSeparator());
            }

        } catch (IOException e) {
            System.out.println("Error llegint el fitxer: " + e.getMessage());
            return;
        }

        //Escribe el archivo de nuevo pero con la modificacion
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(sb.toString());
            System.out.println("Nota modificada correctament.");
        } catch (IOException e) {
            System.out.println("Error escrivint al fitxer: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            String directory = "C:/";  
            String fileName = "NotasDAM2.txt";            
            String alumneModificar = "ELM RAMIS NAVARRO";
            //Llamada a encontrar el archvo
            String result = findFile(directory, fileName);
            
            //Comprueba si lo ha encontrado
            if (result != null) {
                System.out.println("Archivo encontrado en: " + result);
                //Guarda la ruta en una variable
                String filePath = Paths.get(result).getParent().toString() + File.separator + "NotasDAM2.txt";
                //Llamada a modificar el archivo
                modificarNota(filePath, alumneModificar, '5');
            } else {
                System.out.println("Archivo no encontrado.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
