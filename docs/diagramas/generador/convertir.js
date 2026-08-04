// Convierte los SVG a PNG al doble de escala, para que se vean nitidos
// tanto en pantalla como impresos en el documento.
const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

const origen = 'bpmn';
const destino = 'png';
fs.mkdirSync(destino, { recursive: true });

(async () => {
  const svgs = fs.readdirSync(origen).filter(f => f.endsWith('.svg')).sort();
  let ok = 0;
  for (const f of svgs) {
    const salida = path.join(destino, f.replace('.svg', '.png'));
    try {
      const info = await sharp(path.join(origen, f), { density: 192 })
        .png({ compressionLevel: 9 })
        .toFile(salida);
      console.log(`  ${f.replace('.svg','').slice(0,48).padEnd(48)} ${info.width}x${info.height}`);
      ok++;
    } catch (e) {
      console.log(`  FALLO ${f}: ${e.message}`);
    }
  }
  console.log(`\n${ok}/${svgs.length} convertidos`);
})();
