package com.kolaysoft.grpc_service;

import com.kolaysoft.grpcservice.FileRequest;
import com.kolaysoft.grpcservice.FileResponse;
import com.kolaysoft.grpcservice.FileServiceGrpc;
import com.kolaysoft.grpcservice.JsonRequest;
import io.grpc.stub.StreamObserver;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {

    @Override
    public void writeJsonToFile(JsonRequest request, StreamObserver<FileResponse> responseObserver) {
        String jsonData = request.getJsonData();
        FileResponse.Builder response = FileResponse.newBuilder();

        try (FileWriter fileWriter = new FileWriter("data.json")) {
            fileWriter.write(jsonData);
            response.setMessage("JSON written to disk successfully");
        } catch (IOException e) {
            e.printStackTrace();
            response.setMessage("Failed to write JSON to disk");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FileRequest> uploadFile(StreamObserver<FileResponse> responseObserver) {
        return new StreamObserver<FileRequest>() {
            FileOutputStream fileOutputStream;

            @Override
            public void onNext(FileRequest fileRequest) {
                try {
                    if (fileOutputStream == null) {
                        // Initializing the file output stream on the first chunk
                        fileOutputStream = new FileOutputStream("uploaded_file.bin");
                    }
                    fileOutputStream.write(fileRequest.getFileChunk().toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileResponse response = FileResponse.newBuilder()
                        .setMessage("File uploaded successfully")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }
}
