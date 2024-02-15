import pika

# RabbitMQ connection parameters
credentials = pika.PlainCredentials('your_username', 'your_password')
rabbitmq_host = '192.168.224.128'  # Replace with your RabbitMQ server IP
queue_name = input("Enter the queue name: ")  # Prompt the user to enter the queue name

parameters = pika.ConnectionParameters(rabbitmq_host, credentials=credentials)

# Connect to RabbitMQ server
connection = pika.BlockingConnection(parameters)
channel = connection.channel()

# Declare a queue
channel.queue_declare(queue='hello')

# Publish messages to the queue
while True:
    message = input("Enter message to publish (or 'exit' to quit): ")
    if message.lower() == 'exit':
        break
    channel.basic_publish(exchange='', routing_key='hello', body=message)
    print(" [x] Sent:", message)

# Close connection
connection.close()
