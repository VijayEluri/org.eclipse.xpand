/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtend.profiler;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;

import junit.framework.TestCase;

import org.easymock.IExpectationSetters;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.internal.xpand2.ast.Template;
import org.eclipse.internal.xpand2.model.XpandDefinition;
import org.eclipse.internal.xpand2.parser.XpandParseFacade;
import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xpand2.XpandExecutionContextImpl;
import org.eclipse.xpand2.output.FileHandle;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.Output;
import org.eclipse.xpand2.output.OutputImpl;
import org.eclipse.xpand2.output.VetoStrategy;
import org.eclipse.xtend.XtendFacade;
import org.eclipse.xtend.expression.ExecutionContextImpl;
import org.eclipse.xtend.profiler.profilermodel.Item;
import org.eclipse.xtend.profiler.profilermodel.ModelFactory;
import org.eclipse.xtend.profiler.profilermodel.ProfilingResult;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.Type;
import org.eclipse.xtend.typesystem.emf.EmfMetaModel;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

/**
 * @author Heiko Behrens - Initial contribution and API
 */
public class ProfilerTest extends TestCase implements VetoStrategy {
	// needed for server-side build since encoding of project has been set to ISO-8859-1
	// by convention whereas server ignores this and runs on UTF-8
	private static final String FORCED_ENCODING = "ISO-8859-1";
	
	Profiler profiler = new Profiler();
	Profiler.TimeProvider timeProvider = createStrictMock(Profiler.TimeProvider.class);
	private String lastOutput;
	
	private void expectTimingCalls(int calls) {
		IExpectationSetters<Long> expect = expect(timeProvider.getNanoSeconds());
		for(int i=1; i<=calls; i++)
			expect.andReturn(s2n(i));
		replay(timeProvider);
		
		profiler.setTimeProvider(timeProvider);
	}
	
	private long s2n(int i) {
		return ((long) i) * 1000000000;
	}

	public void testSimple() throws Exception {
		expectTimingCalls(2);
		
		profiler.setTimeProvider(timeProvider);
		profiler.beginRoutine("a");
		profiler.endRoutine();
		
		verify(timeProvider);
	}
	
    private String convertURLToString(URL url) throws Exception { 
        StringBuilder sb = new StringBuilder();

		InputStream is = url.openStream();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, FORCED_ENCODING));

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally {
			is.close();
		}

		return sb.toString(); 
    }
    
	private String loadRessource(String relativeFilename, String className) throws Exception {
    	File caller = new File(className.replace('.', '/') + ".java");
    	String directory = caller.getParent();
    	File file = new File(directory, relativeFilename);
    	URL resource = this.getClass().getClassLoader().getResource(file.toString());
    	if(resource==null)
    		throw new IllegalStateException("No such file " + file);
        return convertURLToString(resource);
	}

    private String loadRessource(String relativeFilename) throws Exception{
    	StackTraceElement element = new RuntimeException().getStackTrace()[1];
    	return loadRessource(relativeFilename, element.getClassName());
    }
    
	private String evaluateDefine(ProfilingResult profilingResult, String resName, String defName) throws Exception {
		Template template = XpandParseFacade.file(getReaderForTemplate(resName), "Foo");
		//OutputStringImpl out = new OutputStringImpl();
		OutputImpl out = new OutputImpl();
		Outlet outlet = new Outlet(".");
		outlet.addVetoStrategy(this);
		out.addOutlet(outlet);
		XpandExecutionContext ctx = createCtx(out);
		XpandDefinition def = template.getDefinitionsByName(defName)[0];
		assertNotNull(def);
		def.evaluate(ctx,profilingResult);
		return this.lastOutput;
	}

	private Reader getReaderForTemplate(String resName) throws Exception {
		String res = loadRessource(resName, org.eclipse.xtend.profiler.templates.Extensions.class.getName());
		return new StringReader(res);
	}

	private XpandExecutionContext createCtx(Output out) {
		final XpandExecutionContextImpl result = new XpandExecutionContextImpl(
				out, null);
		Item item = ModelFactory.eINSTANCE.createItem();
		EPackage modelPackage = item.eClass().getEPackage();//ModelFactory.eINSTANCE.getModelPackage();
		assertNotNull(modelPackage);
		MetaModel mm = new EmfMetaModel(modelPackage);
		result.registerMetaModel(mm);
		result.getResourceManager().setFileEncoding(FORCED_ENCODING);
		return result;
	}

	public void testGProfCycleExample() throws Exception {
		// example from http://www.cs.utah.edu/dept/old/texinfo/as/gprof.html#SEC10
		expectTimingCalls(16);
		
		profiler.beginRoutine("main");  // main
		profiler.beginRoutine("start"); // main->start
		profiler.beginRoutine("a"); 	// main->start->a
		profiler.beginRoutine("b"); 	// main->start->a->b
		profiler.beginRoutine("a"); 	// main->start->[a->b->a]
		profiler.beginRoutine("c"); 	// main->start->[a->b->a]->c
		profiler.endRoutine(); 			// main->start->[a->b->a]
		profiler.endRoutine(); 			// main->start->a->b
		profiler.beginRoutine("c"); 	// main->start->a->b->c
		profiler.endRoutine(); 			// main->start->a->b
		profiler.endRoutine(); 			// main->start->a
		profiler.beginRoutine("c"); 	// main->start->a->c
		profiler.endRoutine(); 			// main->start->a
		profiler.endRoutine(); 			// main->start
		profiler.endRoutine(); 			// main
		profiler.endRoutine(); 
		
		verify(timeProvider);
		
		Item itemMain = profiler.getProfilingResult().getItems().get(0);
		assertEquals("main", itemMain.getName());
		assertEquals(15, n2s(itemMain.getItemTime()));
		assertEquals(2, n2s(itemMain.getSelfTime()));
		assertEquals(15, n2s(profiler.getProfilingResult().getTime()));
		assertEquals(5, profiler.getProfilingResult().getItems().size());
		assertEquals(0, profiler.getProfilingResult().getCycles().size());
		
		CycleDetector detector = new CycleDetector(profiler.getProfilingResult());
		detector.detectCycles();

		assertEquals(15, n2s(profiler.getProfilingResult().getTime()));
		assertEquals(1, profiler.getProfilingResult().getCycles().size());
		
		assertEqualToRessource("GProfCycleExample.txt", "GProf.xpt", "Main");
		
		Item itemA = itemMain;
		assertEquals("main", itemA.getName());
		assertEquals(1, itemA.getSubroutines().size());
		assertEquals(0, itemA.getInvocations().size());
		assertEquals(2, n2s(itemA.getSelfTime()));
		assertEquals(15, n2s(itemA.getTime()));
	}

	private void assertEqualToRessource(String relativeFilename, String resName, String defName)
			throws Exception {
		String expected = loadRessource(relativeFilename);
		
		String actual = evaluateDefine(profiler.getProfilingResult(), resName, defName);
		assertEquals(expected, actual);
	}
	
	public void testOawClassic() throws Exception {
		expectTimingCalls(8);
		profiler.beginRoutine("XPD Root::Root FOR Model");
		profiler.beginRoutine("XPD Root::Root FOR Package");
		profiler.beginRoutine("XPD Root::Root FOR Package");
		profiler.beginRoutine("XPD Root::Root FOR Entity");
		profiler.endRoutine();
		profiler.endRoutine();
		profiler.endRoutine();
		profiler.endRoutine();
		
		verify(timeProvider);
		
		CycleDetector detector = new CycleDetector(profiler.getProfilingResult());
		detector.detectCycles();
		
		assertEqualToRessource("OawClassic.txt", "GProf.xpt", "Main");
		
		String actual = evaluateDefine(profiler.getProfilingResult(), "Html.xpt", "Main");
		assertNotNull("HTML smoke test failed", actual);
	}

	public void testSelfRecursion() throws Exception {
		expectTimingCalls(4);
		profiler.beginRoutine("a");
		profiler.beginRoutine("a");
		profiler.endRoutine();
		profiler.endRoutine();
		
		verify(timeProvider);
		
		CycleDetector detector = new CycleDetector(profiler.getProfilingResult());
		detector.detectCycles();

		assertEqualToRessource("SelfRecursion.txt", "GProf.xpt", "Main");		
	}
	
	public void testSimpleResultTime() throws Exception {
		expectTimingCalls(2);
		profiler.beginRoutine("a");
		profiler.endRoutine();
		verify(timeProvider);

		CycleDetector detector = new CycleDetector(profiler.getProfilingResult());
		detector.detectCycles();
		
		assertEquals(1, n2s(profiler.getProfilingResult().getTime()));
	}
	
	public void testRecursionResultTime() throws Exception {
		expectTimingCalls(4);
		profiler.beginRoutine("a");
		profiler.beginRoutine("a");
		profiler.endRoutine();
		profiler.endRoutine();
		verify(timeProvider);

		CycleDetector detector = new CycleDetector(profiler.getProfilingResult());
		detector.detectCycles();
		
		assertEquals(3, n2s(profiler.getProfilingResult().getTime()));
	}

	private int n2s(long time) {
		return (int)(time / 1000000000);
	}

	public boolean hasVeto(FileHandle handle) {
		this.lastOutput = handle.getBuffer().toString();
		return true;
	}
	
	public void testProfilerInUnitTest() throws Exception {
		// manage execution context manually and wire profiler component
		ExecutionContextImpl ctx = new ExecutionContextImpl();
		ProfilerComponent profComponent = new ProfilerComponent();
		profComponent.prepareProfiler();
		ctx.setVetoableCallBack(profComponent);
		
		// use facade to execute template/extension
		XtendFacade xtdf = XtendFacade.create(ctx, "org::eclipse::xtend::profiler::SimpleXtendFile");
		Object result = xtdf.call("plusOne", 5);
		assertEquals(BigInteger.valueOf(6), result);
		
		// finalizing profiling output
		profComponent.finalizeProfiler();
		
		// work with profiling results, e.g. create report
		String report = evaluateDefine(profComponent.getProfilingResult(), "GProf.xpt", "Main");
		assertTrue(report.contains("XTD org::eclipse::xtend::profiler::SimpleXtendFile::plusOne(Integer) [1]"));
	}
	
	public void testProfilerPackageRegistered() throws Exception {
		// invoke static initializer
		assertNotNull(new ProfilerComponent());
		
		EmfRegistryMetaModel mm = new EmfRegistryMetaModel();
		mm.setTypeSystem(null);
		Type type = mm.getTypeForName("profilermodel::ProfilingResult");
		assertNotNull(type);
	}

}
