package itis.parsing2;

interface FactoryParsingService {

    Factory parseFactoryData(String factoryDataDirectoryPath) throws FactoryParsingException;

}
