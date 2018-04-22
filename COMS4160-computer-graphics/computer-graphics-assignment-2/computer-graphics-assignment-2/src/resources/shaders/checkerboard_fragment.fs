#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;
out int dummy;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;
    Attenuation att;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec3 colour;
    int useColour;
    int hasNormalMap;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = light_intensity  * specularFactor * material.reflectance * vec4(light_colour, 1.0);

    return (diffuseColour + specColour);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

int level = 50;

void main()
{
    vec4 baseColour; 
    if ( material.useColour == 1 )
    {
        baseColour = vec4(material.colour, 1);
    }
    else
    {
        baseColour = texture(texture_sampler, outTexCoord);
    }
    vec4 totalLight = vec4(ambientLight, 1.0);
    totalLight += calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);
    totalLight += calcPointLight(pointLight, mvVertexPos, mvVertexNormal); 
    //  Something like snowflake  
    //      float pattern = sin(outTexCoord.x * level);
    //      for(int i = 0; i < 40; i++)
    //          pattern = tan(pattern);
    //      pattern = pattern * 5;
    
    float pattern = sin(outTexCoord.x * level) * 0.5 + sin(outTexCoord.y * level) * 0.5;
    if(pattern > 0) pattern = 1;
    else pattern = 0;
    //  Different procedural texture:
    //      Checkerboard pattern
    //          float pattern = sin(outTexCoord.x * level) * 0.5 + sin(outTexCoord.y * level) * 0.5;
    //          if(pattern > 0) 
    //             pattern = 1;
    //          else pattern = 0;

    //      Special pattern
    //          float pattern = sin(outTexCoord.x * level + outTexCoord.y * level) * 0.5;
    //          float pattern = sin(outTexCoord.x * 60) * sin(outTexCoord.x * 60) * sin(outTexCoord.x * 60 * outTexCoord.y) * sin(outTexCoord.x * 60 * outTexCoord.y) + noise1(outTexCoord.y) * 100;
              float pattern = tan(outTexCoord.x * 30) * sin(outTexCoord.x * 60) * sin(outTexCoord.x * 60 * outTexCoord.y) * sin(outTexCoord.x * 60 * outTexCoord.y) + noise1(outTexCoord.y) * 100;
    
    fragColor = baseColour * totalLight * pattern;
    dummy = material.hasNormalMap;
}