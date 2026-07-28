export class RatioParser {
	public parse(ratio: string): number {
		const match = /(\d+):(\d+)$/g.exec(ratio);
		if (match) {
			const dividend = parseInt(match[1]);
			const divisor = parseInt(match[2]);
			return dividend / divisor;
		}
		throw new Error(`ratio ${ratio} is invalid!`);
	}
}
