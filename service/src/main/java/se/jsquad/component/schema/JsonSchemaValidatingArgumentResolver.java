/*
 * Copyright 2021 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.component.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.swagger2.Swagger2Module;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.MethodParameter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import se.jsquad.exception.BadRequestRuntimeException;
import se.jsquad.validator.ValidateJsonSchema;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JsonSchemaValidatingArgumentResolver implements HandlerMethodArgumentResolver {
    private final ObjectMapper objectMapper;
    
    public JsonSchemaValidatingArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return  methodParameter.getParameterAnnotation(ValidateJsonSchema.class) != null;
    }
    
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        String validationResultMessage = "Failed to validate JSON schema.";
        final ValidateJsonSchema validateJsonSchema = methodParameter.getParameterAnnotation(ValidateJsonSchema.class);
        
        if (validateJsonSchema != null) {
            Swagger2Module swagger2Module = new Swagger2Module();
            SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion
                .DRAFT_2019_09, OptionPreset.PLAIN_JSON);
            SchemaGeneratorConfig schemaGeneratorConfig = schemaGeneratorConfigBuilder.with(swagger2Module).build();
            SchemaGenerator schemaGenerator = new SchemaGenerator(schemaGeneratorConfig);
            JsonNode json = objectMapper.readTree(getJsonPayload(nativeWebRequest));
    
            JsonSchema jsonSchema = getJsonSchema(schemaGenerator.generateSchema(Class.forName(validateJsonSchema
                .xsdClass().getName())).toString());
            
            Set<ValidationMessage> validationMessages = jsonSchema.validate(json);
            
            if (validationMessages.isEmpty()) {
                return objectMapper.treeToValue(json, methodParameter.getParameterType());
            }
            
            validationResultMessage = validationMessages.toString();
        }
        
        throw new BadRequestRuntimeException(validationResultMessage);
    }
    
    private String getJsonPayload(NativeWebRequest nativeWebRequest) throws IOException {
        HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        return StreamUtils.copyToString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8);
    }
    
    private JsonSchema getJsonSchema(String schemaJson) {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        
        try (InputStream schemaStream = new ByteArrayInputStream(schemaJson.getBytes())) {
            return jsonSchemaFactory.getSchema(schemaStream);
        } catch (Exception e) {
            throw new BadRequestRuntimeException(e.getMessage(), e);
        }
    }
}
