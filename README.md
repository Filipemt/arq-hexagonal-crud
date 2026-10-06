# Hexagonal Orders (Spring Boot)
Requisitos: Java 17+ e Maven.

    mvn test               # testes (domínio e casos de uso, sem subir Spring)
    mvn spring-boot:run    # sobe a API em http://localhost:8080

## O que foi acrescentado em relação ao CRUD

| Conceito | Onde ver |
|---|---|
| Value objects com validação (`Money`, `Quantity`, ids tipados) | `domain/model` |
| Aggregate root com regras e máquina de estados (`Order`) | `domain/model/Order.java` |
| Domain service (`DiscountPolicy`) | `domain/service` |
| Eventos de domínio (`OrderPlaced`, `OrderPaid`, `OrderCancelled`) | `domain/event` |
| Exceções de negócio com hierarquia | `domain/exception` |
| Um caso de uso por classe, só orquestrando | `application/service` |
| Várias ports outbound (repo, catálogo, estoque, pagamento, eventos) | `application/port/out` |
| Adapters mockados | `adapter/out/*` |
| Validação de formato no adapter + tradução de exceção em HTTP | `adapter/in/web` |
| Testes: domínio puro, e casos de uso com fakes | `src/test` |

## Estrutura

    domain/
      model/        Order, OrderItem, Money, Quantity, Customer, ids...
      service/      DiscountPolicy
      event/        OrderPlaced, OrderPaid, OrderCancelled
      exception/    DomainException, NotFoundException, InvalidOrderStateException ...
    application/
      port/in/      PlaceOrder, PayOrder, CancelOrder, GetOrder (use cases + command)
      port/out/     OrderRepository, CustomerRepository, ProductCatalog,
                    StockService, PaymentGateway, EventPublisher
      service/      PlaceOrderService, PayOrderService, CancelOrderService, GetOrderService
    adapter/
      in/web/       OrderController, DTOs, GlobalExceptionHandler
      out/          persistence/ (repos em memória), external/ (catálogo, estoque, pagamento fakes),
                    events/ (publisher que só imprime no console)
    config/         BeanConfig (liga ports aos casos de uso)

## Onde fica cada tipo de validação

| Tipo | Exemplo | Onde |
|---|---|---|
| Formato da entrada | `customerId` nulo, lista de itens vazia | `PlaceOrderRequest` (Bean Validation) |
| Invariante do domínio | quantidade > 0, dinheiro não negativo, desconto <= 30% | value objects e `Order` |
| Regra de negócio | pedido cancelado não pode ser pago | `Order` |
| Regra com dado externo | cliente existe, produto existe, estoque suficiente | caso de uso (via ports) |
| Regra entre entidades | desconto por tipo de cliente e valor | `DiscountPolicy` |

## Dados fixos para testar

Clientes
- Ana (VIP):      11111111-1111-1111-1111-111111111111
- Bruno (REGULAR): 22222222-2222-2222-2222-222222222222

Produtos (preço / estoque)
- Notebook: aaaaaaaa-0000-0000-0000-000000000001  (R$ 3500,00 / 5)
- Mouse:    aaaaaaaa-0000-0000-0000-000000000002  (R$ 80,00 / 50)
- Teclado:  aaaaaaaa-0000-0000-0000-000000000003  (R$ 250,00 / 20)

## Cenários para experimentar

    # 1) Pedido da Ana (VIP) com 1 teclado -> desconto de 10%
    curl -X POST localhost:8080/orders -H "Content-Type: application/json" -d '{
      "customerId":"11111111-1111-1111-1111-111111111111",
      "items":[{"productId":"aaaaaaaa-0000-0000-0000-000000000003","quantity":1}]}'

    # 2) Pagar (troque <id>)
    curl -X POST localhost:8080/orders/<id>/pay

    # 3) Cancelar um pedido pago -> estorno + libera estoque (veja o console)
    curl -X POST localhost:8080/orders/<id>/cancel

    # 4) Cancelar de novo -> 409 (regra do domínio)
    curl -X POST localhost:8080/orders/<id>/cancel

    # 5) Estoque insuficiente -> 409
    curl -X POST localhost:8080/orders -H "Content-Type: application/json" -d '{
      "customerId":"22222222-2222-2222-2222-222222222222",
      "items":[{"productId":"aaaaaaaa-0000-0000-0000-000000000001","quantity":6}]}'

    # 6) Pagamento recusado (acima de R$ 5000 no fake) -> 422
    #    pedido de 2 notebooks do Bruno e depois /pay

    # 7) Cliente inexistente -> 404 ; corpo inválido -> 400

Status HTTP usados: 201, 200, 400 (formato/argumento), 404 (não encontrado),
409 (regra de negócio), 422 (pagamento recusado).

## Roteiro de estudo sugerido

1. Leia `Order.java`: repare que as regras vivem aqui, não no service.
2. Leia `PlaceOrderService`: é uma sequência de passos (buscar, delegar ao domínio, reservar, salvar, publicar).
3. Leia `OrderUseCasesTest`: os casos de uso são testados sem Spring, trocando adapters por fakes.
4. Troque `FakePaymentGateway` por outra implementação de `PaymentGateway` e veja que o núcleo não muda.
5. Confira que `domain/` e `application/` não importam nada de `org.springframework`.

## Simplificações conscientes (para manter didático)

- Repositório em memória guarda o próprio objeto de domínio (sem mapeamento). Com JPA, haveria entidade JPA e conversão, como no projeto anterior.
- Sem transações (não há banco). Em um caso real, o limite transacional seria o caso de uso.
- O publisher de eventos é chamado depois de salvar. Em produção, avaliar o padrão Outbox para garantir consistência.
- Sem autenticação/autorização.
