using Polar.Communication.WebSocket;
using Polar.Communication.WebSocket;
using SharedPacketLib;
using System;
using System.Net.Sockets;

namespace ConnectionManager
{
    public class ConnectionInformation : IDisposable
    {
        private readonly string _ip;
        private readonly int _connectionID;
        private bool _isConnected;
        private readonly AsyncCallback _sendCallback;

        public IDataParser parser;
        public event ConnectionChange connectionClose;
        private Socket _dataSocket;
        private byte[] _buffer;

        // Nuevo: timestamp de último recibo
        public DateTime LastReceiveUtc { get; private set; }
        public delegate void ConnectionChange(ConnectionInformation information);
        public bool IsWebSocket;

        // Configurables
        private static readonly TimeSpan DefaultInactivityThreshold = TimeSpan.FromMinutes(5); // ejemplo
        public TimeSpan InactivityThreshold { get; set; } = DefaultInactivityThreshold;

        public ConnectionInformation(IDataParser parser, Socket dataStream, string ip, int connectionID)
        {
            this.parser = parser;
            this._buffer = new byte[GameSocketManagerStatics.BUFFER_SIZE];

            this._dataSocket = dataStream;

            // Desactivar timeouts de socket para que no lancen excepciones por pausas de tráfico
            // Usaremos control de inactividad a nivel de aplicación con LastReceiveUtc
            try
            {
                this._dataSocket.SendTimeout = 0;
                this._dataSocket.ReceiveTimeout = 0;

                // Habilitar TCP keepalive (sensible en servidores Windows/Linux).
                // Nota: SIO_KEEPALIVE_VALS / IOControl puede usarse para ajustar intervalos en Windows.
                this._dataSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.KeepAlive, true);

                // (Opcional) Ajuste fino de keepalive vía IOControl (Windows)
                // var keepAlive = new byte[12];
                // BitConverter.GetBytes((uint)1).CopyTo(keepAlive, 0); // enable
                // BitConverter.GetBytes((uint)60000).CopyTo(keepAlive, 4); // keepalive time (ms)
                // BitConverter.GetBytes((uint)10000).CopyTo(keepAlive, 8); // keepalive interval (ms)
                // this._dataSocket.IOControl(IOControlCode.KeepAliveValues, keepAlive, null);
            }
            catch
            {
                // Si no se pueden fijar opciones, no romper; continuar con defaults
            }

            this._sendCallback = new AsyncCallback(this.SentData);

            this._ip = ip;
            this._connectionID = connectionID;

            // Inicializar LastReceiveUtc
            this.LastReceiveUtc = DateTime.UtcNow;
        }

        public string getIp() => this._ip;

        public int getConnectionID() => _connectionID;

        public void disconnect()
        {
            try
            {
                if (!this._isConnected) return;

                this._isConnected = false;

                if (this._dataSocket != null)
                {
                    try
                    {

                        if (this._dataSocket.Connected)
                        {
                            this._dataSocket.Shutdown(SocketShutdown.Both);
                            this._dataSocket.Close();
                        }
                    }

                    catch { }

                    this._dataSocket.Dispose();
                }

                if (this.parser != null)
                    this.parser.Dispose();

                if (this.connectionClose != null)
                    this.connectionClose(this);

                this.connectionClose = null;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }

        public void startPacketProcessing()
        {
            if (this._isConnected) return;

            this._isConnected = true;

            try
            {
                // Inicializar timestamp al empezar
                this.LastReceiveUtc = DateTime.UtcNow;

                this._dataSocket.BeginReceive(this._buffer, 0, this._buffer.Length, SocketFlags.None, this._incomingDataPacket, (object)this._dataSocket);
            }
            catch
            {
                this.disconnect();
            }
        }

        private void _incomingDataPacket(IAsyncResult iAr)
        {
            if (!_isConnected) return;

            int length = 0;

            try
            {
                length = this._dataSocket.EndReceive(iAr);
            }
            catch
            {
                // No desconectar inmediatamente: pero EndReceive típicamente falla si la conexión está rota,
                // por compatibilidad, desconectamos aquí.
                this.disconnect();
                return;
            }

            if (length == 0)
            {
                // 0 bytes indica cierre remoto del socket -> desconectar
                this.disconnect();
                return;
            }

            // Actualizar timestamp de último dato recibido
            this.LastReceiveUtc = DateTime.UtcNow;

            if (this.parser == null) return;

            try
            {
                byte[] packet = new byte[length];
                Array.Copy(this._buffer, packet, length);

                this.parser.handlePacketData(packet);
            }
            catch
            {
                this.disconnect();
            }
            finally
            {
                try
                {
                    // Reanudar la recepción de datos
                    this._dataSocket.BeginReceive(this._buffer, 0, this._buffer.Length, SocketFlags.None, new AsyncCallback(this._incomingDataPacket), (object)this._dataSocket);
                }
                catch
                {
                    this.disconnect();
                }
            }
        }

        // Añadir este método en la clase ConnectionInformation (junto al SendData existente):
        // Nuevo: comprobación a nivel de aplicación si la conexión está inactiva
        public bool IsInactive()
        {
            try
            {
                return (DateTime.UtcNow - this.LastReceiveUtc) > this.InactivityThreshold;
            }
            catch
            {
                return false;
            }
        }
        public void SendData(byte[] packet, int offset, int length)
        {
            if (!this._isConnected) return;

            try
            {
                if (this.IsWebSocket)
                {
                    // Para WebSocket: copiar sólo la porción necesaria y codificar
                    byte[] fragment = new byte[length];
                    Buffer.BlockCopy(packet, offset, fragment, 0, length);

                    byte[] encoded = EncodeDecode.EncodeMessage(fragment);

                    // Pasar 'encoded' como state para evitar GC hurrying
                    this._dataSocket.BeginSend(encoded, 0, encoded.Length, SocketFlags.None, _sendCallback, encoded);
                }
                else
                {
                    this._dataSocket.BeginSend(packet, offset, length, SocketFlags.None, _sendCallback, null);
                }
            }
            catch
            {
                this.disconnect();
            }
        }

        public void SendData(byte[] packet)
        {
            if (packet == null) return;
            SendData(packet, 0, packet.Length);
        }
        private void SentData(IAsyncResult iAr)
        {
            try
            {
                this._dataSocket.EndSend(iAr);
            }
            catch
            {
                this.disconnect();
            }
        }

        public void Dispose()
        {
            if (this._isConnected)
                this.disconnect();
        }
    }
}