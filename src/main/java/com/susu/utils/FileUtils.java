package com.susu.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>Description: File Toolset</p>
 * <p>文件工具类</p>
 * @author sujay
 * @version 22:11 2022/1/24
 * @since JDK1.8 <br/>
 */
public class FileUtils {

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * <p>Description: read file</p>
     * <p>读取文件</p>
     *
     * @param path 文件路径  File path
     *             <tr><th align="left">Value</th><th align="left">Meaning</th></tr>
     *             <tr><td valign="top"><tt>"r"</tt></td>
     *             <td>	以只读的方式打开文本，也就意味着不能用write来操作文件 {@link
     *             IOException}</td></tr>
     *             <tr><td valign="top"><tt>"rw"</tt></td>
     *             <td> 读操作和写操作都是允许的</td></tr>
     *             <tr><td valign="top"><tt>"rws"</tt></td>
     *             <td> 每当进行写操作，同步的刷新到磁盘，刷新内容和元数据</td></tr>
     *             <tr><td valign="top"><tt>"rwd"&nbsp;&nbsp;</tt></td>
     *             <td> 每当进行写操作，同步的刷新到磁盘，刷新内容 </td></tr>
     */
    public static ByteBuffer readBuffer(String path) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path, "r"); FileInputStream fis =
                new FileInputStream(raf.getFD()); FileChannel channel = fis.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate((int) raf.length());
            channel.read(buffer);
            buffer.flip();
            return buffer;
        }
    }

    /**
     * <p>Description: read file</p>
     * <p>读取文件</p>
     *
     * @param path 文件路径  File path
     */
    public static String readString(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static File getFile(String path) {
        return new File(path);
    }

    /**
     * <p>Description: Get file extension</p>
     * <p>获取文件名后缀</p>
     *
     * @param filename 文件名
     */
    public static String getExtension(String filename) {
        String[] split = filename.split("\\.");
        if (split.length > 1) {
            return split[split.length -1];
        }
        return null;
    }

    /**
     * <p>Description: Writing files in NiO mode</p>
     * <p>NIO方式写文件</p>
     *
     * @param path       文件路径
     * @param delOldFile 是否删除旧文件
     * @param buffer     内存缓冲区
     */
    public static void writeFile(String path, boolean delOldFile, ByteBuffer buffer) throws IOException {
        mkdirParent(path);
        File file = new File(path);
        if (delOldFile) {
            if (file.exists()) {
                del(file);
            }
        }
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw"); FileOutputStream fos =
                new FileOutputStream(raf.getFD()); FileChannel channel = fos.getChannel()) {
            channel.write(buffer);
            channel.force(true);
        }
    }

    /**
     * <p>Description: The parent directory where the file was created already exists</p>
     * <p>创建文件的父目录存在</p>
     *
     * @param fileName 文件名
     */
    public static void mkdirParent(String fileName) {
        File file = new File(fileName);
        mkdirs(file.getParent());
    }

    /**
     * <p>Description: Create directory recursively</p>
     * <p>递归创建目录</p>
     *
     * @param pathName 目录名
     */
    public static void mkdirs(String pathName) {
        File file = new File(pathName);
        file.mkdirs();

    }

    /**
     * <p>Description: is Directory</p>
     * <p>是否是文件夹</p>
     *
     * @param path          资源路径
     * @param isFollowLinks 是否需要处理符号链接的选项
     * @return boolean  true / false
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        } else {
            LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
            return Files.isDirectory(path, options);
        }
    }

    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
    }

    /**
     * <p>Description: delete file</p>
     * <p>删除文件</p>
     *
     * @param path 资源路径
     */
    protected static void delFile(Path path) throws IOException {
        try {
            Files.delete(path);
        } catch (AccessDeniedException e) {
            if (!path.toFile().delete()) {
                throw e;
            }
        }

    }

    /**
     * <p>Description: delete file</p>
     * <p>删除文件</p>
     *
     * @param file 文件
     */
    public static boolean del(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                boolean isOk = clean(file);
                if (!isOk) {
                    return false;
                }
            }
            Path path = file.toPath();
            try {
                delFile(path);
            } catch (DirectoryNotEmptyException e) {
                del(path);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Description: delete file</p>
     * <p>删除文件</p>
     *
     * @param path 资源路径
     */
    public static boolean del(Path path) {
        if (Files.notExists(path)) {
            return true;
        } else {
            try {
                if (isDirectory(path)) {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            if (exc == null) {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            } else {
                                throw exc;
                            }
                        }
                    });
                } else {
                    delFile(path);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * <p>Description: Empty file</p>
     * <p>清空文件</p>
     */
    public static boolean clean(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File childFile : files) {
                    if (!del(childFile)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>Description: Compress to zip</p>
     * <p>压缩成ZIP</p>
     *
     * @param inPath           压缩文件夹路径
     * @param outPath          压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String inPath, String outPath, boolean keepDirStructure) throws RuntimeException {
        File source = new File(inPath);
        ZipOutputStream zos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outPath);
            zos = new ZipOutputStream(fos);

            compress(null, source, zos, keepDirStructure);
        } catch (Exception e) {
            throw new RuntimeException("zip error from FileUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @param source           源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception 流异常
     */
    private static void compress(String name, File source, ZipOutputStream zos, boolean keepDirStructure) throws Exception {
        String path = (name != null) ? name : source.getName();
        if (source.isFile()) {
            zos.putNextEntry(new ZipEntry(path));
            int len;
            FileInputStream in = new FileInputStream(source);
            byte[] buf = new byte[BUFFER_SIZE];
            while ((len = in.read(buf, 0, buf.length)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = source.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (keepDirStructure) {
                    zos.putNextEntry(new ZipEntry(path + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (keepDirStructure) {
                        compress(path + "/" + file.getName(), file, zos, true);
                    } else {
                        compress(file.getName(), file, zos, false);
                    }

                }
            }
        }
    }

    /**
     * <p>Description: Compress to zip</p>
     * <p>压缩成ZIP</p>
     *
     * @param inPaths 压缩文件夹路径
     * @param outPath 压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String outPath, String... inPaths) throws RuntimeException {
        ZipOutputStream zos = null;
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outPath);
            zos = new ZipOutputStream(fos);
            byte[] buf = new byte[BUFFER_SIZE];
            for (String path : inPaths) {
                File file = new File(path);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int len;
                FileInputStream in = new FileInputStream(file);
                while ((len = in.read(buf, 0, buf.length)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from FileUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>Description: Unzip the package</p>
     * <p>解压压缩包</p>
     *
     * @param path 文件路径
     */
    public static void unZip(String path) {
        File source = new File(path);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            try {
                zis = new ZipInputStream(new FileInputStream(source));
                ZipEntry entry;
                byte[] buf = new byte[BUFFER_SIZE];
                while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
                    File target = new File(source.getParent(), entry.getName());
                    if (!target.getParentFile().exists()) {
                        target.getParentFile().mkdirs();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(target));
                    int read;
                    while ((read = zis.read(buf, 0, buf.length)) != -1) {
                        bos.write(buf, 0, read);
                    }
                    bos.flush();
                }
                zis.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("unzip error from FileUtils", e);
            } finally {
                if (zis != null) {
                    try {
                        zis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * <p>Description: Copy Folder</p>
     *
     * @param source 源文件路口
     * @param target 目标文件路径
     */
    public static void copyDir(String source, String target) {

        File sourceDir = new File(source);

        if (!sourceDir.exists()) {
            throw new RuntimeException("File not found");
        }

        if (!sourceDir.isDirectory()) {
            throw new RuntimeException("Is not a folder");
        }

        File targetDirLast = new File(target + File.separator + sourceDir.getName());

        if (!targetDirLast.exists()) {
            targetDirLast.mkdir();
        }

        copyDirFiles(source, targetDirLast.getAbsolutePath());
    }

    /**
     * <p>Description: Copy Folder</p>
     *
     * @param source 源文件路口
     * @param target 目标文件路径
     */
    public static void copyDirFiles(String source, String target) {
        File sourDir = new File(source);

        File[] fileList = sourDir.listFiles();

        if (fileList == null || fileList.length < 1) {
            return;
        }


        for (File file : fileList) {
            String temp = target + File.separator + file.getName();

            if (file.isFile()) {
                copy(file.getAbsolutePath(), temp);
            } else if (file.isDirectory()) {
                File tempAid = new File(temp);
                if (!tempAid.exists()) {
                    tempAid.mkdir();
                }
                copyDirFiles(file.getAbsolutePath(), temp);
            }

        }
    }

    /**
     * <p>Description: File copy</p>
     *
     * @param source 源文件路口
     * @param target 目标文件路径
     */
    public static void copy(String source, String target) {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);

            byte[] bytes = new byte[1024 * 1024];

            int readData = 0;

            while ((readData = in.read(bytes)) != -1) {
                out.write(bytes, 0, readData);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] toBytes(File file) {
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream in = new FileInputStream(file)){
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static File toFile(String filename, byte[] bytes) {
        File file = new File(filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 获取指定文件最后修改时间
     *
     * @param file  目标文件
     */
    public static Date lastModifiedTime(File file) {
        if (null == file || !file.exists()) {
            return null;
        }
        return new Date(file.lastModified());
    }

    /**
     * <p>Description: Calculate the total size of a directory or file</p>
     *
     * 计算目录或文件的总大小
     *
     * @param file  目标文件
     * @return size 文件大小 bytes 字节长度
     */
    public static long size(File file) {
        if (null != file && file.exists() && !Files.isSymbolicLink(file.toPath())) {
            if (!file.isDirectory()) {
                return file.length();
            } else {
                long size = 0L;
                File[] subFiles = file.listFiles();
                if (subFiles == null || subFiles.length == 0) {
                    return 0L;
                } else {
                    for (File subFile : subFiles) {
                        size += size(subFile);
                    }
                    return size;
                }
            }
        } else {
            return 0L;
        }
    }
}
