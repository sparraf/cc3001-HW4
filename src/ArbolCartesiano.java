import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;

public class ArbolCartesiano {
	
	//Clase tupla que modela el par ordenado (x,y)
	public class Tupla {
		
		private int x;
		private float y;
		
		//Constructor
		public Tupla(int x_in, float y_in) {
			x = x_in;
			y = y_in;
		}
		
		private void imprimirTupla(StringBuilder constructor) {
			String s = "(" + x + "," + y + ")";
			constructor.append(s);
		}
	}
	
	// Clase lista enlazada para implementar una Pila
	public class nodoLista {
		
		private ArbolCartesiano val;
		private nodoLista sig;
		
		// Constructor
		public nodoLista(ArbolCartesiano valor, nodoLista siguiente) {
			val = valor;
			sig = siguiente;
		}
	}
	
	// Clase Pila para recorrer el arbol cartesiano
	public class Pila {
		
		private nodoLista lista;
		
		// Constructor
		public Pila() {
			lista = null;
		}
		
		// Metodo de instancia, agrega un elemento a la pila (no retorna nada)
		public void apilar (ArbolCartesiano elemento) {
			lista = new nodoLista(elemento, lista);
		}
		
		// Metodo de instancia, remueve el elemento del tope de la pila y lo
		// devuelve
		public ArbolCartesiano desapilar() {
			ArbolCartesiano nodo = null;
			if(lista != null) {
				nodo = lista.val;
				lista = lista.sig;
				return nodo;
			}
			return nodo;
		}
	}
	
	private Tupla val;
	private ArbolCartesiano izq;
	private ArbolCartesiano der;
	
	//Constructor
	public ArbolCartesiano(Tupla valor, ArbolCartesiano izquierdo, ArbolCartesiano derecho) {
		val = valor;
		izq = izquierdo;
		der = derecho;
	}
	
	//Metodo de instancia, inserta un par ordenado al arbol cartesiano
	public void insertar(int x, float y) {
		//Paso 1: Realizar insercion ABB
		//Recorrer arbol hasta llegar al nivel inferior (insercion ABB)
		ArbolCartesiano nodoActual = this;
		Pila pilaArbol = new Pila();
		while (nodoActual.val != null) {
			//Armar una pila con todo el camino de nodos por el que se paso
			pilaArbol.apilar(nodoActual);
			if (x < nodoActual.val.x) {
				nodoActual = nodoActual.izq;
			}
			else {
				nodoActual = nodoActual.der;
			}
		}
		//Caso base: Si el nodo esta vacio, agregar el elemento
		nodoActual.val = new Tupla(x,y);
		nodoActual.izq = new ArbolCartesiano(null, null, null);
		nodoActual.der = new ArbolCartesiano(null, null, null);
				
		//Paso 2: Ubicar nodo segun su prioridad en y (estilo heap)
		//Crear booleano que indica si el nodo esta bien ubicado segun prioridad
		boolean bienUbicado = false;
		while(!bienUbicado) {
			//Revisar prioridad nodo padre
			ArbolCartesiano nodoPadre = pilaArbol.desapilar();
			//Si no hay padre (el nodo actual es la raiz), no hacer nada
			if(nodoPadre == null) {
				break;
			}
			//Si el padre tiene mayor prioridad, hacer una rotacion simple
			if(nodoPadre.val.y > nodoActual.val.y) {
				Tupla valPadre = nodoPadre.val;
				//Caso 1: El hijo se encuentra a la derecha
				if(nodoActual.val.x > nodoPadre.val.x) {
					ArbolCartesiano actualIzq = nodoActual.izq;
					ArbolCartesiano padreIzq = nodoPadre.izq;
					nodoPadre.val = nodoActual.val;
					nodoActual.val = valPadre;
					nodoPadre.izq = nodoActual;
					nodoPadre.der = nodoActual.der;
					nodoActual.izq = padreIzq;
					nodoActual.der = actualIzq;
				}
				//Caso 2: El hijo se encuentra a la izquierda
				else {
					ArbolCartesiano actualDer = nodoActual.der;
					ArbolCartesiano padreDer = nodoPadre.der;
					nodoPadre.val = nodoActual.val;
					nodoActual.val = valPadre;
					nodoPadre.der = nodoActual;
					nodoPadre.izq = nodoActual.izq;
					nodoActual.der = padreDer;
					nodoActual.izq = actualDer;
				}
				//Actualizar nodo actual
				nodoActual = nodoPadre;
			}
			else {
				bienUbicado = true;
			}
		}
	}
	
	//Metodo de instancia, crea un string con los nodos del arbol en posorden, 
	//segun las convencion especificada en el enunciado de la tarea
	private void imprimirArbol(StringBuilder constructor) {
		if(val == null) {
			//Si el nodo esta vacio, imprimir el string "[]"
			constructor.append("[]");
			return;
		}
		else {
			//Si el nodo no esta vacio, imprimir el hijo izquierdo, el hijo
			//derecho, y luego el valor del nodo
			izq.imprimirArbol(constructor);
			der.imprimirArbol(constructor);
			val.imprimirTupla(constructor);
			return;
		}
	}
	
	//Metodo de instancia, imprime los nodos del arbol en postorden
	public void imprimir() {
		StringBuilder sb = new StringBuilder();
		imprimirArbol(sb);
		System.out.println(sb);
	}
	
	//Metodo de instancia, calcula el costo de busqueda de los sub arboles de un
	//arbol cartesiano segun la altura en la que se encuentra el nodo con 
	//respecto a la raiz
	private float calcularCosto(int altura) {
		//Verificar si el nodo tiene una llave
		if(val == null) {
			return 0;
		}
		else {
			//Calcular costo del nodo
			float costoNodo = 1 + altura;
			costoNodo += izq.calcularCosto(altura + 1) + der.calcularCosto(altura + 1);
			return costoNodo;
		}
	}
	
	//Metodo de instancia, calcula el numero de llaves en el arbol cartesiano
	private int numeroNodos() {
		//Verificar si el nodo tiene una llave
		if(val == null) {
			return 0;
		}
		else {
			return 1 + izq.numeroNodos() + der.numeroNodos();
		}
	}
	
	//Metodo de instancia, calcula el costo promedio de busqueda de las llaves 
	//del arbol y lo imprime
	public float costoPromedio() {
		float costoTotal = calcularCosto(0);;
		int numeroNodos = numeroNodos();
		return costoTotal / numeroNodos;
	}

	public static void main(String[] args) {
		
		//Determinar cual parte de la tarea se quiere hacer
		boolean usarTreap = false;
		
		if(!usarTreap) {
			//Crear arbol cartesiano
			ArbolCartesiano arbol = new ArbolCartesiano(null, null, null);
			
			// Guardar directorio del archivo
			System.out.println("Ingrese el directorio del archivo que desea leer");
			Scanner entrada = new Scanner(System.in);
			String directorio = entrada.nextLine();
			entrada.close();
			
			//Leer archivo
			try {
				Scanner archivo = new Scanner(new File(directorio));
				while(archivo.hasNextLine()) {
					String tupla = archivo.nextLine();
					//Obtener strings de las coordenadas del par ordenado
					String[] coordenadas = tupla.split(" ");
					int x = Integer.parseInt(coordenadas[0]);
					float y = Float.parseFloat(coordenadas[1]);
					//Insertar tupla en el arbol
					arbol.insertar(x, y);
				}
				archivo.close();
				//Luego de leer el archivo, imprimir el arbol
				arbol.imprimir();
				System.out.println(arbol.costoPromedio());
			}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			//Ingresar n
			int n = 1024;
			Random rand = new Random();
			//Realizar procedimiento 10 veces para cada n
			for(int j=0; j < 10; ++j) {
				//Crear arbol cartesiano
				ArbolCartesiano arbol = new ArbolCartesiano(null, null, null);
				//Agregar tupla (i, random) donde i={0, ..., n}
				for(int i=1; i <= n; ++i) {
					float y = rand.nextFloat();
					arbol.insertar(i, y);
				}
				//Imprimir costo promedio
				System.out.println(arbol.costoPromedio());
			}
		}
	}
}
