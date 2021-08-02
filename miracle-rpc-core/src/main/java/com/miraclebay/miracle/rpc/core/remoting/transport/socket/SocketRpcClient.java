package com.miraclebay.miracle.rpc.core.remoting.transport.socket;

import com.miraclebay.miracle.rpc.common.exception.RpcException;
import com.miraclebay.miracle.rpc.common.extension.ExtensionLoader;
import com.miraclebay.miracle.rpc.core.registry.ServiceDiscovery;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;
import com.miraclebay.miracle.rpc.core.remoting.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Extension;

@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient(){
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //send data to the server
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //read RpcResponse from the input stream
            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new RpcException("调用服务失败", e);
        }
    }
}