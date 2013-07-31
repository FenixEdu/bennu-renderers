package pt.ist.bennu.core.presentationTier.servlets;

import java.util.Collection;

public class ActionServletConfiguration {

    public ResourceConfig resources;
    public Collection<ModuleConfiguration> modules;
    public Collection<ExceptionHandler> exceptionHandlers;

    public static class ResourceConfig {
        public Collection<String> bundles;
        public String defaultBundle;
    }

    public static class ModuleConfiguration {
        public String name;
        public String configFile;
    }

    public static class ExceptionHandler {
        public String handler;
        public Collection<ExceptionHandlerMapping> mappings;
    }

    public static class ExceptionHandlerMapping {
        public String type;
        public String key;
    }

}
