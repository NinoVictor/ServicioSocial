/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.DocumentosDAO;

import java.util.List;
import modelo.Documento;

/**
 *
 * @author Vik-t
 */
public interface IDocumento {
    public List<Documento> getDocumentos(int idSeguimiento);
}
