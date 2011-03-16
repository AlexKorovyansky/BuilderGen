package info.piwai.buildergen.generation;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;

public class ResourceCodeWriterTest {

	/**
	 * This test is just for the fun of using Mockito. It does not really test
	 * the external behavior of the method.
	 */
	@Test
	public void usesFilerToOpenStream() throws IOException {

		Filer filer = mock(Filer.class);
		FileObject fileObject = mock(FileObject.class);
		when(filer.createResource(Mockito.<Location> any(), anyString(), anyString())).thenReturn(fileObject);

		OutputStream expectedOS = mock(OutputStream.class);
		when(fileObject.openOutputStream()).thenReturn(expectedOS);

		ResourceCodeWriter resourceCodeWriter = new ResourceCodeWriter(filer);
		JPackage jPackage = new JCodeModel()._package("some.package");
		OutputStream resultingOS = resourceCodeWriter.openBinary(jPackage, null);

		assertSame(expectedOS, resultingOS);
	}

	@Test
	public void doesNotCallOutputStreamCloseMethod() throws IOException {

		Filer filer = mock(Filer.class);
		FileObject fileObject = mock(FileObject.class);
		when(filer.createResource(Mockito.<Location> any(), anyString(), anyString())).thenReturn(fileObject);

		OutputStream expectedOS = mock(OutputStream.class);
		when(fileObject.openOutputStream()).thenReturn(expectedOS);

		ResourceCodeWriter resourceCodeWriter = new ResourceCodeWriter(filer);
		JPackage jPackage = new JCodeModel()._package("some.package");
		resourceCodeWriter.openBinary(jPackage, null);

		doThrow(new RuntimeException("Close method should not be called")).when(expectedOS).close();

		resourceCodeWriter.close();
	}
	
	@Test
	public void multipleCallsAreDelegatedToFiler() throws IOException{
		Filer filer = mock(Filer.class);

		FileObject fileObject = mock(FileObject.class);
		when(filer.createResource(Mockito.<Location> any(), anyString(), anyString())).thenReturn(fileObject);
		
		ResourceCodeWriter resourceCodeWriter = new ResourceCodeWriter(filer);
		JPackage jPackage = new JCodeModel()._package("some.package");
		resourceCodeWriter.openBinary(jPackage, null);
		resourceCodeWriter.openBinary(jPackage, null);
		
		 verify(filer, times(2)).createResource(Mockito.<Location> any(), anyString(), anyString());

	}

}
