package com.rodrigopeleias.pontointeligente.api.controllers;

import com.rodrigopeleias.pontointeligente.api.entities.Empresa;
import com.rodrigopeleias.pontointeligente.api.services.EmpresaService;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmpresaControllerTest {

    public static final String BUSCAR_EMPRESA_CNPJ_URL = "/api/empresas/cnpj/";
    public static final Long ID = Long.valueOf(1);
    private static final String CNPJ = "51463645000100";
    private static final String RAZAO_SOCIAL = "Empresa XYZ";
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmpresaService empresaService;

    @Test
    @WithMockUser
    public void testBuscarEmpresaCnpjInvalido() throws Exception {
        given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Empresa não encontrada para o CNPJ " + CNPJ));
    }

    @Test
    @WithMockUser
    public void testBuscarEmpresaCnpjValido() throws Exception {
        given(this.empresaService.buscarPorCnpj(Mockito.anyString()))
                .willReturn(Optional.of(this.obterDadosEmpresa()));

        mvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.razaoSocial", CoreMatchers.equalTo(RAZAO_SOCIAL)))
                .andExpect(jsonPath("$.data.cnpj", CoreMatchers.equalTo(CNPJ)))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    private Empresa obterDadosEmpresa() {
        return Empresa.builder()
                .id(ID)
                .razaoSocial(RAZAO_SOCIAL)
                .cnpj(CNPJ)
                .build();
    }
}
