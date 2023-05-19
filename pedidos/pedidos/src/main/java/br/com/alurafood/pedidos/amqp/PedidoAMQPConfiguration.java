package br.com.alurafood.pedidos.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoAMQPConfiguration {

    //conversao padrao
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    //criação da fila
    @Bean
    public Queue filaDetalhesPedido(){
        return QueueBuilder
                .nonDurable("pagamentos.detalhes-pedido")
                .build();
    }

    //criação da Exchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return ExchangeBuilder
                .fanoutExchange("pagamentos.ex")
                .build();
    }


    //ligação
    @Bean
    public Binding bindPagamentoPedido(FanoutExchange fanoutExchange){
        return BindingBuilder.bind(filaDetalhesPedido())
                .to(fanoutExchange);
    }

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

}
