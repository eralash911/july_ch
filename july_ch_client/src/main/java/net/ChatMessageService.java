package net;


import java.io.IOException;

public class ChatMessageService {
    NetworkService networkService;
    MessageProcessor messageProcessor;

    public ChatMessageService(MessageProcessor messageProcessor){
        this.messageProcessor = messageProcessor;
    }

    public void connect(){
      try{
          this.networkService = new NetworkService(this);
          networkService.readMessages();
      }catch (IOException e) {
          e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.networkService != null && this.networkService.getSocket().isConnected();
    }
    public void receive(String message) {
    }
}
