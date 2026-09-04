package pe.edu.upeu.PharmaBackend.exception;

public class StockInsuficienteException extends ReglaNegocioException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}