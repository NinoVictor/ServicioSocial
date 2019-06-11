/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import dao.DocumentosDAO.DocumentosImp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import modelo.Documento;
import modelo.Inscripcion;

/**
 * FXML Controller class
 *
 * @author Vik-t
 */
public class AgregarDocumentoController implements Initializable {

    @FXML
    private JFXTextArea txtDescripcion;
    @FXML
    private JFXDatePicker txtFecha;
    @FXML
    private JFXComboBox<String> cbTipoDoc;
    @FXML
    private JFXButton btnSubirDoc;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private Label lbArchivoAdjunto;

    private Inscripcion inscripcion;

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;  
        txtFecha.setValue(LocalDate.now());
        try {
            txtFecha.setOverLay(true);
        }catch(Exception ex) {
            System.out.println("Error en DatePicker");
        }
        
    }
    @FXML
    private void clicBtnCancelar() {
        Stage ventana = (Stage)btnCancelar.getScene().getWindow();
        ventana.close();
    }
    @FXML
    private void llenarCbTipoDocumento() {
         List<String> listaMes = new ArrayList();
        listaMes.add("Registro y plan de Actividades");
        listaMes.add("Reporte Mensual de Actividades");
        listaMes.add("Carta Aceptación");
        listaMes.add("Carta Liberación");
        listaMes.add("Horario");
        listaMes.add("Memoria");

        ObservableList<String> obsevableListaTipoOpe
                = FXCollections.observableArrayList(listaMes);
        cbTipoDoc.setItems(obsevableListaTipoOpe);
    }
    private File archivo;
    @FXML
    private void clicBtnSubirDocumento() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Stage stage = new Stage();
        archivo = fileChooser.showOpenDialog(stage);
        lbArchivoAdjunto.setText(archivo.getName());
        
    }
    
    public File guardarDocumento(){
        Path origen = Paths.get(archivo.getAbsolutePath());
        Path destino = Paths.get("src\\main\\resources\\fxml\\documentos\\" + archivo.getName());
        try {
            Files.copy(origen, destino,StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e) {
            return null;
        }
        return destino.toFile();
    }
    
    public Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }
    @FXML
    private void clicBtGuardar() {
       if(validarCampos()) {
           File copiaArchivo = guardarDocumento();
           if(copiaArchivo != null) {
               Documento documento = new Documento();
               documento.setTipo(cbTipoDoc.getSelectionModel().getSelectedItem());
               documento.setDescripcion(txtDescripcion.getText());
               documento.setEstado("Pendiente");
               documento.setIdSeguimiento(inscripcion.getSeguimiento().getIdSeguimiento());
               documento.setLink(copiaArchivo.getPath());
               documento.setFecha(asDate(txtFecha.getValue()));
               DocumentosImp documentoimp = new DocumentosImp();
               if(documentoimp.guardarDocumento(documento)) {
                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exito");
                    alert.setHeaderText("El documento se ha guardado exitosamente");
                    alert.showAndWait();
                    Stage ventana = (Stage)btnCancelar.getScene().getWindow();
                    ventana.close();
               } else {
                   Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error en BD");
                    alert.setHeaderText("Hubo un error en la conexion a la Base de Datos,"
                            + "no se pudo guardar el documento, intente más tarde");
                    alert.showAndWait();
               }
           }else {
               System.out.println("Error al copiar el archivo, no se pudo realizar el guardoado");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error al Guardar Documento");
                alert.setHeaderText("Hubo un error al guardar el documento,"
                        + "Intente más tarde");
                alert.showAndWait();
           }
       }
       
    }
    
    public boolean validarCampos() {
        if(cbTipoDoc.getValue() == null) {
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Campos Vacíos");
           alert.setHeaderText("Seleccionar el tipo de documento antes de guardar");
           alert.showAndWait();
           return false;
        }
        if(txtDescripcion.getText().isEmpty()) {
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Campos Vacíos");
           alert.setHeaderText("Ingresar la descripcion del documento antes de guardar");
           alert.showAndWait();
           return false;
        }
        if(archivo == null) {
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Campos Vacíos");
           alert.setHeaderText("Ingresar el documento antes de guardar");
           alert.showAndWait();
           return false;
        }
        
        return true;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        llenarCbTipoDocumento();
    }    
    
}
