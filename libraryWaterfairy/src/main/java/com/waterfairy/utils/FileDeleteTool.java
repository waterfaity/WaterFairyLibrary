package com.waterfairy.utils;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/3/6 10:40
 * @info: 文件删除工具
 */
public class FileDeleteTool {
    private OnFileDeleteListener onFileDeleteListener;

    public FileDeleteTool() {
    }

    public FileDeleteTool(OnFileDeleteListener onFileDeleteListener) {
        this.onFileDeleteListener = onFileDeleteListener;
    }


    public void setOnFileDeleteListener(OnFileDeleteListener onFileDeleteListener) {
        this.onFileDeleteListener = onFileDeleteListener;
    }

    public void execute(String... filePath) {
        execute(true, true, filePath);
    }

    public void execute(boolean deleteRootPath, boolean deleteChildPath, String... filePaths) {
        if (filePaths == null || filePaths.length <= 0) {
            if (onFileDeleteListener != null) onFileDeleteListener.onFileDeleteError("文件路径为空!");
        } else {
            File[] files = new File[filePaths.length];
            for (int i = 0; i < filePaths.length; i++) {
                files[i] = new File(filePaths[i]);
            }
            execute(deleteRootPath, deleteChildPath, files);
        }
    }

    public void execute(File file) {
        execute(true, true, file);
    }

    /**
     * @param file            删除指定文件
     * @param deleteRootPath  是否要删除主路径
     * @param deleteChildPath 删除文件夹 (不包含rootPath)
     */
    public void execute(boolean deleteRootPath, boolean deleteChildPath, File... file) {
        if (file == null) {
            if (onFileDeleteListener != null) onFileDeleteListener.onFileDeleteError("文件为空");
        } else {
            startAsync(deleteRootPath, deleteChildPath, file);
        }
    }


    /**
     * @param file
     * @param deleteRootPath  是否要删除主路径(文件上层文件夹)
     * @param deleteChildPath 文件夹
     */
    private synchronized void startAsync(boolean deleteRootPath, boolean deleteChildPath, final File... file) {
        new AsyncTask<Object, String, String>() {
            @Override
            protected String doInBackground(Object... files) {
                try {
                    File[] filesTemp = (File[]) files[0];
                    for (File file : filesTemp) {
                        deleteRoot(file, (boolean) files[1], (boolean) files[2]);
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }

            /**
             * 删除主文件
             * @param file
             * @param deleteRootPath
             * @param deleteChildPath
             * @throws IOException
             */
            private void deleteRoot(File file, boolean deleteRootPath, boolean deleteChildPath) throws IOException {

                if (file.isFile()) {
                    //删除文件
                    file.delete();
                } else if (file.isDirectory()) {
                    //删除文件夹下的文件
                    File[] files = file.listFiles();
                    for (File fileTemp : files) {
                        //删除子文件
                        deleteFile(fileTemp, deleteChildPath);
                    }
                    if (deleteRootPath) {
                        boolean delete = file.delete();
                        if (!delete) {
                            //删除失败
                            throw new IOException(file.getAbsolutePath());
                        }
                    }
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (onFileDeleteListener != null)
                    onFileDeleteListener.onFileDeleteProgress(values[0]);
            }

            /**
             * 当前长度
             *
             * @param file 要搜索的文件
             * @param deletePath 是否要删除路径
             */
            private void deleteFile(File file, boolean deletePath) throws IOException {
                if (file.isDirectory()) {
                    //查询路径下的文件以及文件夹
                    File[] files = file.listFiles();
                    for (File tempFile : files) {
                        deleteFile(tempFile, deletePath);
                    }
                    if (deletePath) {
                        boolean delete = file.delete();
                        if (!delete) {
                            throw new IOException(file.getAbsolutePath());
                        }
                    }

                } else {
                    //查询到文件
                    boolean delete = file.delete();
                    if (!delete) {
                        throw new IOException(file.getAbsolutePath());
                    }
                    publishProgress(file.getAbsolutePath());
                }
            }

            @Override
            protected void onPostExecute(String filePath) {
                super.onPostExecute(filePath);
                if (onFileDeleteListener != null)
                    if (filePath != null) {
                        onFileDeleteListener.onFileDeleteError(filePath);
                    } else {
                        onFileDeleteListener.onFileDeleteSuccess();
                    }
            }
        }.execute(new Object[]{file, deleteRootPath, deleteChildPath});
    }


    public interface OnFileDeleteListener {
        void onFileDeleteSuccess();

        void onFileDeleteProgress(String filePath);

        void onFileDeleteError(String msg);
    }
}
