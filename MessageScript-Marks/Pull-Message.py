import pika

# RabbitMQ connection parameters
credentials = pika.PlainCredentials('test', 'test')
parameters = pika.ConnectionParameters('', credentials=credentials)

# Connect to RabbitMQ server
connection = pika.BlockingConnection(parameters)
channel = connection.channel()

# Declare a queue
channel.queue_declare(queue='hello')

# Define a callback function to handle received messages
def callback(ch, method, properties, body):
    print(" [x] Received:", body)

# Consume messages from the queue
channel.basic_consume(queue='hello', on_message_callback=callback, auto_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')

# Start consuming messages
channel.start_consuming()
