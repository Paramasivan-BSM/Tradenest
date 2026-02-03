package com.bsm.tradenest.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration

public class Dbconfig {



    @Bean
    public MongoClient mongoclient(){
        return MongoClients.create("mongodb+srv://TradeNest:TradeNest@tradenest.i1rju9z.mongodb.net/?appName=TradeNest");
    }

    @Bean
    public MongoDatabase db(MongoClient client){

        return  client.getDatabase("TradeNest");
    }


}
